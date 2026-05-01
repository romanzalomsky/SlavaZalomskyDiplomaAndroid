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
import com.zalomsky.sportscore.features.bottom_container.favorite.FavoriteSearchTeamAdapter
import com.zalomsky.sportscore.features.bottom_container.favorite.FavoriteTeamAdapter
import com.zalomsky.sportscore.features.bottom_container.favorite.FavoriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class UserPersonFragment : Fragment() {

    private val viewModel: FavoriteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var searchEditText: android.widget.EditText
    private lateinit var searchResultsRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var favoriteResultsRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var favoriteListTitleTextView: android.widget.TextView
    private lateinit var logoutButton: android.widget.Button

    private lateinit var searchAdapter: FavoriteSearchTeamAdapter
    private lateinit var favoriteAdapter: FavoriteTeamAdapter
    private var searchJob: Job? = null
    private val SEARCH_DELAY_MS = 500L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_person, container, false)

        searchEditText = view.findViewById(R.id.searchEditText)
        searchResultsRecyclerView = view.findViewById(R.id.teamSearchResultsRecyclerView)
        favoriteResultsRecyclerView = view.findViewById(R.id.favoriteTeamsRecyclerView)
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

        favoriteResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoriteAdapter
        }

        setupSearchListener()
        observeViewModel()
        logoutButton.setOnClickListener {
            authViewModel.logout()
            val options = navOptions { popUpTo(R.id.nav_graph) { inclusive = true } }
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.authFragment, null, options)
        }

        viewModel.loadFavoriteTeams()
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

    private fun setupSearchListener() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()

                searchJob?.cancel()

                if (query.length >= 2) {
                    searchJob = MainScope().launch {
                        delay(SEARCH_DELAY_MS)
                        viewModel.searchTeamsSimple(query)
                    }
                } else {
                    searchAdapter.updateList(emptyList())
                    searchResultsRecyclerView.visibility = View.GONE
                    favoriteListTitleTextView.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { teams ->
            if (searchEditText.text.toString().trim().length < 2) {
                return@observe
            }

            searchAdapter.updateList(teams)

            if (teams.isNotEmpty()) {
                searchResultsRecyclerView.visibility = View.VISIBLE
                favoriteListTitleTextView.visibility = View.GONE
            } else {
                searchResultsRecyclerView.visibility = View.GONE

                favoriteListTitleTextView.visibility = View.VISIBLE
            }
        }

        viewModel.favoriteTeams.observe(viewLifecycleOwner) { teamList ->

            favoriteAdapter.updateList(teamList)

            if (searchEditText.text.toString().trim().isEmpty()) {
                favoriteListTitleTextView.visibility = View.VISIBLE
            }
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
    }
}