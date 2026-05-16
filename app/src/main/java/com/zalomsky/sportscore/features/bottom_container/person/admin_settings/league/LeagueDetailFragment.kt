package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.league

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.Country
import com.zalomsky.sportscore.domain.models.LeagueModel
import com.zalomsky.sportscore.domain.models.SportType
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.player.LeaguePlayersAdapter
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.player.PlayerSearchAdapter
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.team.LeagueTeamsAdapter
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.team.TeamSearchAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LeagueDetailFragment : Fragment() {

    private lateinit var leagueNameEditText: TextInputEditText
    private lateinit var leagueImageEditText: TextInputEditText
    private lateinit var countryAutoCompleteTextView: AutoCompleteTextView
    private lateinit var updateLeagueButton: Button

    private lateinit var leagueTeamsRecyclerView: RecyclerView
    private lateinit var teamSearchEditText: EditText
    private lateinit var searchResultsRecyclerView: RecyclerView

    private lateinit var labelLeagueTeams: TextView
    private lateinit var labelSearchTeams: TextView

    private lateinit var leagueTeamsAdapter: LeagueTeamsAdapter
    private lateinit var teamSearchAdapter: TeamSearchAdapter
    private lateinit var leaguePlayersAdapter: LeaguePlayersAdapter
    private lateinit var playerSearchAdapter: PlayerSearchAdapter

    private val viewModel: LeagueViewModel by viewModels()
    private val args: LeagueDetailFragmentArgs by navArgs()

    private var isTennis: Boolean = false
    private var searchJob: Job? = null
    private var selectedCountryId: String? = null
    private var countryList: List<Country> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_league_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        viewModel.getCountriesList()

        val leagueId = args.leagueId
        viewModel.getLeagueDetails(leagueId)

        setupRecyclerViews()
        setupSearch()
        observeViewModel()
        setupUpdateListener(leagueId)
    }

    private fun initViews(view: View) {
        leagueNameEditText = view.findViewById(R.id.leagueNameEditTextDetails)
        leagueImageEditText = view.findViewById(R.id.leagueImageEditTextDetails)
        countryAutoCompleteTextView = view.findViewById(R.id.countryAutoCompleteTextViewLeagueDetails)
        updateLeagueButton = view.findViewById(R.id.updateLeagueButton)
        leagueTeamsRecyclerView = view.findViewById(R.id.leagueTeamsRecyclerView)
        teamSearchEditText = view.findViewById(R.id.teamSearchEditText)
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView)
        labelLeagueTeams = view.findViewById(R.id.labelLeagueTeams)
        labelSearchTeams = view.findViewById(R.id.labelSearchTeams)
    }

    private fun setupRecyclerViews() {
        leagueTeamsAdapter = LeagueTeamsAdapter(emptyList())
        leaguePlayersAdapter = LeaguePlayersAdapter(emptyList())

        teamSearchAdapter = TeamSearchAdapter(emptyList()) { teamId ->
            assignTeamToLeague(teamId, args.leagueId)
        }
        playerSearchAdapter = PlayerSearchAdapter(emptyList()) { playerId ->
            assignPlayerToLeague(playerId, args.leagueId)
        }
    }

    private fun assignTeamToLeague(teamId: String, leagueId: String) {
        viewModel.assignTeamToLeague(teamId, leagueId) {
            Toast.makeText(context, "Команда добавлена в лигу!", Toast.LENGTH_SHORT).show()
            viewModel.loadLeagueTeams(leagueId)
            teamSearchEditText.setText("")
        }
    }

    private fun assignPlayerToLeague(playerId: String, leagueId: String) {
        viewModel.assignPlayerToLeague(playerId, leagueId) {
            Toast.makeText(context, "Игрок добавлен в лигу!", Toast.LENGTH_SHORT).show()
            viewModel.loadLeaguePlayers(leagueId)
            teamSearchEditText.setText("")
        }
    }

    private fun setupSearch() {
        teamSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                val query = s.toString().trim()
                val currentLeagueId = args.leagueId
                if (query.isNotEmpty()) {
                    searchJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(300)
                        if (isTennis) {
                            viewModel.searchPlayers(query, currentLeagueId)
                        } else {
                            viewModel.searchTeams(query, currentLeagueId)
                        }
                    }
                } else {
                    teamSearchAdapter.updateList(emptyList())
                    playerSearchAdapter.updateList(emptyList())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.countries.observe(viewLifecycleOwner) { countries ->
            countryList = countries
            setupCountryDropdown(countries)
        }

        viewModel.currentLeague.observe(viewLifecycleOwner) { league ->
            if (league != null) {
                leagueNameEditText.setText(league.leagueName)
                leagueImageEditText.setText(league.leagueImage)
                isTennis = league.sportType.equals(SportType.TENNIS.name, true)

                updateLabels(isTennis)
                updateAdapters(isTennis)
                viewModel.loadLeagueParticipants(league.id, isTennis)

                val currentCountry = countryList.find { it.id == league.countryId }
                if (currentCountry != null) {
                    countryAutoCompleteTextView.setText(currentCountry.name, false)
                    selectedCountryId = currentCountry.id
                }
            } else {
                Toast.makeText(context, "Лига не найдена или произошла ошибка загрузки.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        viewModel.leagueTeams.observe(viewLifecycleOwner) { teams ->
            if (!isTennis) leagueTeamsAdapter.updateList(teams)
        }

        viewModel.leaguePlayers.observe(viewLifecycleOwner) { players ->
            if (isTennis) leaguePlayersAdapter.updateList(players)
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { teams ->
            if (!isTennis) teamSearchAdapter.updateList(teams)
        }

        viewModel.playerSearchResults.observe(viewLifecycleOwner) { players ->
            if (isTennis) playerSearchAdapter.updateList(players)
        }
    }

    private fun updateLabels(isTennis: Boolean) {
        if (isTennis) {
            labelLeagueTeams.text = "Игроки в лиге"
            labelSearchTeams.text = "Добавить игрока в лигу"
            teamSearchEditText.hint = "Поиск игрока по имени"
        } else {
            labelLeagueTeams.text = "Команды в лиге"
            labelSearchTeams.text = "Добавить команду в лигу"
            teamSearchEditText.hint = "Поиск команды по названию"
        }
    }

    private fun updateAdapters(isTennis: Boolean) {
        if (isTennis) {
            leagueTeamsRecyclerView.adapter = leaguePlayersAdapter
            searchResultsRecyclerView.adapter = playerSearchAdapter
        } else {
            leagueTeamsRecyclerView.adapter = leagueTeamsAdapter
            searchResultsRecyclerView.adapter = teamSearchAdapter
        }
    }

    private fun setupCountryDropdown(countries: List<Country>) {
        val countryNames = countries.map { it.name }
        val adapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, countryNames)

        countryAutoCompleteTextView.setAdapter(adapter)

        countryAutoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            selectedCountryId = countries.find { it.name == selectedName }?.id
        }
    }

    private fun setupUpdateListener(leagueId: String) {
        updateLeagueButton.setOnClickListener {
            val name = leagueNameEditText.text.toString().trim()
            val image = leagueImageEditText.text.toString().trim()
            val countryId = selectedCountryId

            if (name.isEmpty() || image.isEmpty() || countryId == null) {
                Toast.makeText(context, "Заполните все поля.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedLeague = LeagueModel(
                id = leagueId,
                leagueName = name,
                leagueImage = image,
                countryId = countryId
            )

            viewModel.updateLeague(leagueId, updatedLeague) {
                Toast.makeText(context, "Лига успешно обновлена!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }
}