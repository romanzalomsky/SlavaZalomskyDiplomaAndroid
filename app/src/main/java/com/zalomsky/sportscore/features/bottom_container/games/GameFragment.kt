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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.LeagueResponseModel
import com.zalomsky.sportscore.domain.models.responses.LeaguesUiState
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
    private lateinit var matchAdapter: MatchAdapter
    private lateinit var leagueSpinner: Spinner

    private var leagueList: List<LeagueResponseModel> = emptyList()

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
        leagueSpinner.visibility = View.GONE

        matchAdapter = MatchAdapter(emptyList())
        matchesRecyclerView.adapter = matchAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLeagues()
        observeLeagueSchedule()
    }

    private fun observeLeagues() {
        viewModel.leaguesState
            .onEach { state ->
                when (state) {
                    is LeaguesUiState.Loading -> {
                        leagueSpinner.visibility = View.GONE
                        loadingProgressBar.visibility = View.VISIBLE
                    }
                    is LeaguesUiState.Success -> {
                        loadingProgressBar.visibility = View.GONE
                        leagueSpinner.visibility = View.VISIBLE
                        leagueList = state.leagues
                        setupLeagueSpinner(state.leagues)
                    }
                    is LeaguesUiState.Error -> {
                        loadingProgressBar.visibility = View.GONE
                        leagueSpinner.visibility = View.GONE
                        errorTextView.visibility = View.VISIBLE
                        errorTextView.text = state.message
                    }
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setupLeagueSpinner(leagues: List<LeagueResponseModel>) {
        if (leagues.isEmpty()) return

        val leagueNames = leagues.map { it.leagueName }
        val context = requireContext()
        val adapter = ArrayAdapter(
            context,
            R.layout.spinner_selected_item,
            leagueNames
        ).apply {
            setDropDownViewResource(R.layout.spinner_dropdown_item)
        }
        leagueSpinner.adapter = adapter
        leagueSpinner.visibility = View.VISIBLE

        leagueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLeague = leagues[position]
                viewModel.loadLeagueSchedule(selectedLeague.id)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun observeLeagueSchedule() {

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
                        } else {
                            matchesRecyclerView.visibility = View.VISIBLE
                            (matchesRecyclerView.adapter as? MatchAdapter)?.updateMatches(state.matches)
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