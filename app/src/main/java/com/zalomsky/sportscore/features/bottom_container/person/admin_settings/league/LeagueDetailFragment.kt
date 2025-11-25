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

    private lateinit var leagueTeamsAdapter: LeagueTeamsAdapter
    private lateinit var teamSearchAdapter: TeamSearchAdapter

    private val viewModel: LeagueViewModel by viewModels()

    private val args: LeagueDetailFragmentArgs by navArgs()

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

        setupTeamRecyclerViews()
        setupTeamSearch()

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
    }

    private fun setupTeamRecyclerViews() {
        leagueTeamsAdapter = LeagueTeamsAdapter(emptyList())
        leagueTeamsRecyclerView.adapter = leagueTeamsAdapter

        teamSearchAdapter = TeamSearchAdapter(emptyList()) { teamId ->
            assignTeamToLeague(teamId, args.leagueId)
        }
        searchResultsRecyclerView.adapter = teamSearchAdapter
    }

    private fun assignTeamToLeague(teamId: String, leagueId: String) {
        viewModel.assignTeamToLeague(teamId, leagueId) {
            Toast.makeText(context, "Команда добавлена в лигу!", Toast.LENGTH_SHORT).show()
            viewModel.getLeagueDetails(leagueId)
            teamSearchEditText.setText("")
        }
    }

    private fun setupTeamSearch() {
        teamSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                searchJob?.cancel()

                val query = s.toString().trim()
                val currentLeagueId = args.leagueId
                if (query.isNotEmpty()) {
                    searchJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(300)
                        viewModel.searchTeams(query, currentLeagueId)
                    }
                } else {
                    teamSearchAdapter.updateList(emptyList())
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
            leagueTeamsAdapter.updateList(teams)
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { teams ->
            teamSearchAdapter.updateList(teams)
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