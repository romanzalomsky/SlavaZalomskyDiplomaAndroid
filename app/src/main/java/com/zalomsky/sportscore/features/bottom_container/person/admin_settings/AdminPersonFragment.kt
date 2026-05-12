package com.zalomsky.sportscore.features.bottom_container.person.admin_settings

import android.text.Editable
import android.text.TextWatcher
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.data.UserRepositoryImpl
import com.zalomsky.sportscore.features.auth.AuthViewModel
import com.zalomsky.sportscore.features.bottom_container.favorite.FavoritePlayerAdapter
import com.zalomsky.sportscore.features.bottom_container.favorite.FavoriteSearchPlayerAdapter
import com.zalomsky.sportscore.features.bottom_container.favorite.FavoriteSearchTeamAdapter
import com.zalomsky.sportscore.features.bottom_container.favorite.FavoriteTeamAdapter
import com.zalomsky.sportscore.features.bottom_container.favorite.FavoriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.fragment.app.viewModels
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class AdminPersonFragment : Fragment() {

    @Inject
    lateinit var userRepository: UserRepositoryImpl

    private val authViewModel: AuthViewModel by viewModels()
    private val favoriteViewModel: FavoriteViewModel by viewModels()

    private lateinit var usersAdapter: AdminUsersAdapter
    private lateinit var searchAdapter: FavoriteSearchTeamAdapter
    private lateinit var playerSearchAdapter: FavoriteSearchPlayerAdapter
    private lateinit var favoriteAdapter: FavoriteTeamAdapter
    private lateinit var favoritePlayerAdapter: FavoritePlayerAdapter

    private var searchJob: Job? = null
    private var playerSearchJob: Job? = null
    private val SEARCH_DELAY_MS = 500L

    private lateinit var searchEditText: EditText
    private lateinit var playerSearchEditText: EditText
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var playerSearchResultsRecyclerView: RecyclerView
    private lateinit var favoriteResultsRecyclerView: RecyclerView
    private lateinit var favoritePlayersRecyclerView: RecyclerView
    private lateinit var favoriteListTitle: TextView
    private lateinit var favoritePlayersTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingsContent = view.findViewById<View>(R.id.settingsContent)
        val usersRecyclerView = view.findViewById<RecyclerView>(R.id.adminUsersRecyclerView)
        val cabinetContent = view.findViewById<View>(R.id.cabinetContent)
        val toggleGroup = view.findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup)

        searchEditText = view.findViewById(R.id.searchEditText)
        playerSearchEditText = view.findViewById(R.id.playerSearchEditText)
        searchResultsRecyclerView = view.findViewById(R.id.teamSearchResultsRecyclerView)
        playerSearchResultsRecyclerView = view.findViewById(R.id.playerSearchResultsRecyclerView)
        favoriteResultsRecyclerView = view.findViewById(R.id.favoriteTeamsRecyclerView)
        favoritePlayersRecyclerView = view.findViewById(R.id.favoritePlayersRecyclerView)
        favoriteListTitle = view.findViewById(R.id.favoriteListTitle)
        favoritePlayersTitle = view.findViewById(R.id.favoritePlayersTitle)

        val linkToCountry = view.findViewById<View>(R.id.linkToCountryButton)
        val linkToCity = view.findViewById<View>(R.id.linkToCityButton)
        val linkToLeague = view.findViewById<View>(R.id.linkToLeagueButton)
        val linkToPlayer = view.findViewById<View>(R.id.linkToPlayerButton)
        val linkToTeam = view.findViewById<View>(R.id.linkToTeamButton)
        val logoutButton = view.findViewById<View>(R.id.logoutAdminButton)

        usersAdapter = AdminUsersAdapter(emptyList()) { user ->
            user.id?.let { deleteUser(it) }
        }
        usersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        usersRecyclerView.adapter = usersAdapter

        setupFavoriteContent()

        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnSettingsTab -> {
                        settingsContent.isVisible = true
                        usersRecyclerView.isVisible = false
                        cabinetContent.isVisible = false
                    }
                    R.id.btnUsersTab -> {
                        settingsContent.isVisible = false
                        usersRecyclerView.isVisible = true
                        cabinetContent.isVisible = false
                        loadUsers()
                    }
                    R.id.btnCabinetTab -> {
                        settingsContent.isVisible = false
                        usersRecyclerView.isVisible = false
                        cabinetContent.isVisible = true
                        favoriteViewModel.loadFavoriteTeams()
                        favoriteViewModel.loadFavoritePlayers()
                    }
                }
            }
        }

        linkToCountry.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminPersonFragment_to_countryFragment)
        }
        linkToCity.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminPersonFragment_to_cityFragment)
        }
        linkToLeague.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminPersonFragment_to_leagueFragment)
        }
        linkToPlayer.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminPersonFragment_to_playerFragment)
        }
        linkToTeam.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminPersonFragment_to_teamFragment2)
        }
        logoutButton.setOnClickListener {
            authViewModel.logout()
            val options = navOptions { popUpTo(R.id.nav_graph) { inclusive = true } }
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.authFragment, null, options)
        }
    }

    private fun setupFavoriteContent() {
        favoriteAdapter = FavoriteTeamAdapter(
            teams = emptyList(),
            onItemClick = { team ->
                // Assuming Admin can also see team details
                // Need to make sure the action exists in nav_person for AdminPersonFragment
                try {
                    val action = AdminPersonFragmentDirections.actionAdminPersonFragmentToTeamDetailFragment(team.teamId)
                    findNavController().navigate(action)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Переход невозможен", Toast.LENGTH_SHORT).show()
                }
            },
            onDeleteClick = { teamId ->
                favoriteViewModel.deleteTeamFromFavorites(teamId)
            }
        )

        favoriteResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoriteAdapter
        }

        favoritePlayerAdapter = FavoritePlayerAdapter(
            players = emptyList(),
            onItemClick = { },
            onDeleteClick = { favoriteViewModel.deletePlayerFromFavorites(it) }
        )
        favoritePlayersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoritePlayerAdapter
        }

        searchAdapter = FavoriteSearchTeamAdapter(
            teams = emptyList(),
            onFavoriteClick = { team ->
                favoriteViewModel.addTeamToFavorites(team.teamId)
                searchEditText.setText("")
            })
        searchResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }

        playerSearchAdapter = FavoriteSearchPlayerAdapter(
            players = emptyList(),
            onFavoriteClick = { player ->
                favoriteViewModel.addPlayerToFavorites(player.playerId)
                playerSearchEditText.setText("")
            })
        playerSearchResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playerSearchAdapter
        }

        setupSearchListener()
        setupPlayerSearchListener()
        observeFavoriteViewModel()
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
                        favoriteViewModel.searchTeamsSimple(query)
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
                        favoriteViewModel.searchPlayers(query)
                    }
                } else {
                    playerSearchAdapter.updateList(emptyList())
                    playerSearchResultsRecyclerView.visibility = View.GONE
                }
            }
        })
    }

    private fun observeFavoriteViewModel() {
        favoriteViewModel.searchResults.observe(viewLifecycleOwner) { teams ->
            if (searchEditText.text.toString().trim().length < 2) {
                searchResultsRecyclerView.visibility = View.GONE
                return@observe
            }
            searchAdapter.updateList(teams)
            searchResultsRecyclerView.isVisible = teams.isNotEmpty()
        }

        favoriteViewModel.playerSearchResults.observe(viewLifecycleOwner) { players ->
            if (playerSearchEditText.text.toString().trim().length < 2) {
                playerSearchResultsRecyclerView.visibility = View.GONE
                return@observe
            }
            playerSearchAdapter.updateList(players)
            playerSearchResultsRecyclerView.isVisible = players.isNotEmpty()
        }

        favoriteViewModel.favoriteTeams.observe(viewLifecycleOwner) { teamList ->
            favoriteAdapter.updateList(teamList)
        }

        favoriteViewModel.favoritePlayers.observe(viewLifecycleOwner) { playerList ->
            favoritePlayerAdapter.updateList(playerList)
        }

        favoriteViewModel.message.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                favoriteViewModel.clearMessage()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
    }

    private fun loadUsers() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            runCatching { userRepository.getAllUsers() }
                .onSuccess { users ->
                    withContext(Dispatchers.Main) {
                        usersAdapter.submitList(users)
                    }
                }
                .onFailure {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Ошибка загрузки пользователей", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun deleteUser(userId: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            runCatching { userRepository.deleteUser(userId) }
                .onSuccess { response ->
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Пользователь удален", Toast.LENGTH_SHORT).show()
                            loadUsers()
                        } else {
                            Toast.makeText(requireContext(), "Удаление не удалось", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .onFailure {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Ошибка удаления пользователя", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
