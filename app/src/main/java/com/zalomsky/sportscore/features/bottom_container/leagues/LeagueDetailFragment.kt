package com.zalomsky.sportscore.features.bottom_container.leagues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.LeaguesUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LeagueDetailFragment : Fragment() {

    private val viewModel: LeaguesViewModel by viewModels()
    private lateinit var adapter: TeamsHorizontalAdapter
    private lateinit var progressBar: android.widget.ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_league_view, container, false)
        
        progressBar = view.findViewById(R.id.leagueDetailProgressBar)
        val recyclerView = view.findViewById<RecyclerView>(R.id.teamsRecyclerView)
        adapter = TeamsHorizontalAdapter { team ->
            val bundle = Bundle().apply {
                putString("teamId", team.teamId)
            }
            findNavController().navigate(R.id.action_leagueDetailFragment_to_teamDetailFragment, bundle)
        }
        recyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val leagueId = arguments?.getString("leagueId") ?: return
        viewModel.selectLeague(leagueId)

        val nameTitle = view.findViewById<TextView>(R.id.leagueNameTitle)
        val countrySubtitle = view.findViewById<TextView>(R.id.leagueCountrySubtitle)
        
        val statCountryValue = view.findViewById<View>(R.id.statCardCountry).findViewById<TextView>(R.id.statValue)
        val statCountryLabel = view.findViewById<View>(R.id.statCardCountry).findViewById<TextView>(R.id.statLabel)
        val statCountrySub = view.findViewById<View>(R.id.statCardCountry).findViewById<TextView>(R.id.statSublabel)

        val statTeamsValue = view.findViewById<View>(R.id.statCardTeams).findViewById<TextView>(R.id.statValue)
        val statTeamsLabel = view.findViewById<View>(R.id.statCardTeams).findViewById<TextView>(R.id.statLabel)
        val statTeamsSub = view.findViewById<View>(R.id.statCardTeams).findViewById<TextView>(R.id.statSublabel)
        
        val statStatusValue = view.findViewById<View>(R.id.statCardStatus).findViewById<TextView>(R.id.statValue)
        val statStatusLabel = view.findViewById<View>(R.id.statCardStatus).findViewById<TextView>(R.id.statLabel)
        val statStatusSub = view.findViewById<View>(R.id.statCardStatus).findViewById<TextView>(R.id.statSublabel)

        statCountryLabel.text = "СТРАНА"
        statCountrySub.text = "Регион"
        
        statTeamsLabel.text = "КОМАНДЫ"
        statTeamsSub.text = "Участников"
        
        statStatusLabel.text = "СТАТУС"
        statStatusValue.text = "Активен"
        statStatusSub.text = "Состояние"

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.leaguesState.collect { state ->
                progressBar.visibility = if (state is LeaguesUiState.Loading) View.VISIBLE else View.GONE
            }
        }

        viewModel.selectedLeague.onEach { league ->
            league?.let {
                nameTitle.text = it.leagueName
                countrySubtitle.text = it.countryName
                statCountryValue.text = it.countryName
                statTeamsValue.text = it.leagueTeams.size.toString()
                adapter.submitList(it.leagueTeams)
            } ?: run {
                // If leagues are loaded but none selected, it might be an error
                if (viewModel.leaguesState.value is LeaguesUiState.Success) {
                    // android.widget.Toast.makeText(requireContext(), "Лига не найдена", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}