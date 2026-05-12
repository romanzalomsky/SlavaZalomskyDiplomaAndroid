package com.zalomsky.sportscore.features.bottom_container.person.user_settings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.features.auth.AuthViewModel
import com.zalomsky.sportscore.features.bottom_container.favorite.FavoritePlayerAdapter
import com.zalomsky.sportscore.features.bottom_container.favorite.FavoriteSearchPlayerAdapter
import com.zalomsky.sportscore.features.bottom_container.favorite.FavoriteSearchTeamAdapter
import com.zalomsky.sportscore.features.bottom_container.favorite.FavoriteTeamAdapter
import com.zalomsky.sportscore.features.bottom_container.favorite.FavoriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class UserPersonFragment : Fragment() {

    private val viewModel: FavoriteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var searchEditText: android.widget.EditText
    private lateinit var playerSearchEditText: android.widget.EditText
    private lateinit var searchResultsRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var playerSearchResultsRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var favoriteResultsRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var favoritePlayersRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var favoriteListTitleTextView: android.widget.TextView
    private lateinit var logoutButton: android.widget.Button

    private lateinit var searchAdapter: FavoriteSearchTeamAdapter
    private lateinit var playerSearchAdapter: FavoriteSearchPlayerAdapter
    private lateinit var favoriteAdapter: FavoriteTeamAdapter
    private lateinit var favoritePlayerAdapter: FavoritePlayerAdapter
    private var searchJob: Job? = null
    private var playerSearchJob: Job? = null
    private val SEARCH_DELAY_MS = 500L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_person, container, false)

        searchEditText = view.findViewById(R.id.searchEditText)
        playerSearchEditText = view.findViewById(R.id.playerSearchEditText)
        searchResultsRecyclerView = view.findViewById(R.id.teamSearchResultsRecyclerView)
        playerSearchResultsRecyclerView = view.findViewById(R.id.playerSearchResultsRecyclerView)
        favoriteResultsRecyclerView = view.findViewById(R.id.favoriteTeamsRecyclerView)
        favoritePlayersRecyclerView = view.findViewById(R.id.favoritePlayersRecyclerView)
        favoriteListTitleTextView = view.findViewById(R.id.favoriteListTitleTextView)
        logoutButton = view.findViewById(R.id.logoutUserButton)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteAdapter = FavoriteTeamAdapter(
            teams = emptyList(),
            onItemClick = { team ->
                val action = UserPersonFragmentDirections.actionUserPersonFragmentToTeamDetailFragment(team.teamId)
                findNavController().navigate(action)
            },
            onDeleteClick = { teamId ->
                viewModel.deleteTeamFromFavorites(teamId)
            }
        )
        setupSearchRecyclerView()
        setupPlayerSearchRecyclerView()

        favoriteResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoriteAdapter
        }

        favoritePlayerAdapter = FavoritePlayerAdapter(
            players = emptyList(),
            onItemClick = { player ->
                // Add navigation to player detail if available
            },
            onDeleteClick = { playerId ->
                viewModel.deletePlayerFromFavorites(playerId)
            }
        )

        favoritePlayersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoritePlayerAdapter
        }

        setupSearchListener()
        setupPlayerSearchListener()
        observeViewModel()
        logoutButton.setOnClickListener {
            authViewModel.logout()
            val options = navOptions { popUpTo(R.id.nav_graph) { inclusive = true } }
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.authFragment, null, options)
        }

        viewModel.loadFavoriteTeams()
        viewModel.loadFavoritePlayers()
    }

    private fun setupSearchRecyclerView() {
        searchAdapter = FavoriteSearchTeamAdapter(
            teams = emptyList(),
            onFavoriteClick = { team ->
                viewModel.addTeamToFavorites(team.teamId)
                searchEditText.setText("")
            })
        searchResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }
    }

    private fun setupPlayerSearchRecyclerView() {
        playerSearchAdapter = FavoriteSearchPlayerAdapter(
            players = emptyList(),
            onFavoriteClick = { player ->
                viewModel.addPlayerToFavorites(player.playerId)
                playerSearchEditText.setText("")
            })
        playerSearchResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playerSearchAdapter
        }
    }

    private fun setupSearchListener() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                searchJob?.cancel()

                if (query.length >= 2) {
                    searchJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(SEARCH_DELAY_MS)
                        viewModel.searchTeamsSimple(query)
                    }
                } else {
                    searchAdapter.updateList(emptyList())
                    searchResultsRecyclerView.visibility = View.GONE
                }
            }
        })
    }

    private fun setupPlayerSearchListener() {
        playerSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                playerSearchJob?.cancel()

                if (query.length >= 2) {
                    playerSearchJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(SEARCH_DELAY_MS)
                        viewModel.searchPlayers(query)
                    }
                } else {
                    playerSearchAdapter.updateList(emptyList())
                    playerSearchResultsRecyclerView.visibility = View.GONE
                }
            }
        })
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { teams ->
            if (searchEditText.text.toString().trim().length < 2) {
                searchResultsRecyclerView.visibility = View.GONE
                return@observe
            }
            searchAdapter.updateList(teams)
            searchResultsRecyclerView.visibility = if (teams.isNotEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.playerSearchResults.observe(viewLifecycleOwner) { players ->
            if (playerSearchEditText.text.toString().trim().length < 2) {
                playerSearchResultsRecyclerView.visibility = View.GONE
                return@observe
            }
            playerSearchAdapter.updateList(players)
            playerSearchResultsRecyclerView.visibility = if (players.isNotEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.favoriteTeams.observe(viewLifecycleOwner) { teamList ->
            favoriteAdapter.updateList(teamList)
        }

        viewModel.favoritePlayers.observe(viewLifecycleOwner) { playerList ->
            favoritePlayerAdapter.updateList(playerList)
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null && message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
    }
}