package com.zalomsky.sportscore.features.bottom_container.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast // Для временного сообщения
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.LeagueResponseModel
import com.zalomsky.sportscore.domain.models.responses.LeaguesUiState
import com.zalomsky.sportscore.domain.models.responses.MatchResponseModel
import com.zalomsky.sportscore.domain.models.responses.ScheduleUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class GameFragment : Fragment() {

    private val viewModel: GameViewModel by viewModels()

    private lateinit var matchesRecyclerView: RecyclerView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var matchesAdapter: MatchesAdapter
    private lateinit var leagueSpinner: Spinner
    private lateinit var sportSpinner: Spinner
    private lateinit var matchCountText: TextView
    private lateinit var selectedLeagueText: TextView

    private var leagueList: List<LeagueResponseModel> = emptyList()
    private var filteredLeagueList: List<LeagueResponseModel> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game, container, false)
        matchesRecyclerView = view.findViewById(R.id.matchesRecyclerView)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        errorTextView = view.findViewById(R.id.errorTextView)
        leagueSpinner = view.findViewById(R.id.leagueSpinner)
        sportSpinner = view.findViewById(R.id.sportSpinner)
        matchCountText = view.findViewById(R.id.matchCountText)
        selectedLeagueText = view.findViewById(R.id.selectedLeagueText)

        matchesAdapter = MatchesAdapter()
        matchesRecyclerView.layoutManager = LinearLayoutManager(context)
        matchesRecyclerView.adapter = matchesAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLeagueState()
        observeScheduleState()
        setupSportSpinner()
    }

    private fun setupSportSpinner() {
        val sports = listOf("FOOTBALL", "TENNIS", "HOCKEY")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sports)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sportSpinner.adapter = adapter
        sportSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                setupLeagueSpinner(parent.getItemAtPosition(position).toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun observeLeagueState() {
        viewModel.leaguesState
            .onEach { state ->
                when (state) {
                    is LeaguesUiState.Loading -> {
                    }
                    is LeaguesUiState.Success -> {
                        leagueList = state.leagues
                        setupLeagueSpinner(sportSpinner.selectedItem?.toString() ?: "FOOTBALL")
                    }
                    is LeaguesUiState.Error -> {
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setupLeagueSpinner(selectedSport: String) {
        filteredLeagueList = leagueList.filter { it.sportType.equals(selectedSport, true) }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            filteredLeagueList.map { it.leagueName }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        leagueSpinner.adapter = adapter

        leagueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= filteredLeagueList.size) return
                val selectedLeague = filteredLeagueList[position]
                selectedLeagueText.text = selectedLeague.leagueName
                viewModel.loadLeagueSchedule(selectedLeague.id)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun observeScheduleState() {
        viewModel.leagueScheduleState
            .onEach { state ->
                when (state) {
                    is ScheduleUiState.Loading -> {
                        loadingProgressBar.visibility = View.VISIBLE
                        matchesRecyclerView.visibility = View.GONE
                        errorTextView.visibility = View.GONE
                    }
                    is ScheduleUiState.Success -> {
                        loadingProgressBar.visibility = View.GONE
                        errorTextView.visibility = View.GONE

                        if (state.matches.isEmpty()) {
                            errorTextView.text = "Матчи не найдены."
                            errorTextView.visibility = View.VISIBLE
                            matchesRecyclerView.visibility = View.GONE
                            matchCountText.text = "0"
                        } else {
                            matchesRecyclerView.visibility = View.VISIBLE
                            matchesAdapter.submitList(state.matches)
                            matchCountText.text = state.matches.size.toString()
                        }
                    }
                    is ScheduleUiState.Error -> {
                        loadingProgressBar.visibility = View.GONE
                        matchesRecyclerView.visibility = View.GONE
                        errorTextView.visibility = View.VISIBLE
                        errorTextView.text = "Ошибка: ${state.message}"
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}