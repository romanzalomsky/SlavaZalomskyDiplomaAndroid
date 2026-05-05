package com.zalomsky.sportscore.features.bottom_container.leagues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zalomsky.sportscore.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TeamDetailFragment : Fragment() {

    private val viewModel: LeaguesViewModel by viewModels()
    private lateinit var adapter: PlayersAdapter
    private lateinit var progressBar: android.widget.ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_team_view, container, false)
        
        progressBar = view.findViewById(R.id.teamDetailProgressBar)
        val recyclerView = view.findViewById<RecyclerView>(R.id.playersRecyclerView)
        adapter = PlayersAdapter()
        recyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val teamId = arguments?.getString("teamId") ?: return
        viewModel.loadTeamDetails(teamId)

        val icon = view.findViewById<ImageView>(R.id.teamDetailIcon)
        val name = view.findViewById<TextView>(R.id.teamDetailName)
        val country = view.findViewById<TextView>(R.id.detailCountry)
        val city = view.findViewById<TextView>(R.id.detailCity)
        val coach = view.findViewById<TextView>(R.id.detailCoach)
        val stadium = view.findViewById<TextView>(R.id.detailStadium)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isTeamLoading.collect { isLoading ->
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        viewModel.selectedTeam.onEach { team ->
            team?.let {
                name.text = it.teamName
                country.text = it.countryName
                city.text = "Не указано" // Model doesn't have city name, only ID
                coach.text = it.teamCoach
                stadium.text = it.teamStadium
                
                Glide.with(this)
                    .load(it.teamIcon)
                    .into(icon)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.teamPlayers.onEach { players ->
            // Filter players for this team since backend returns all
            val filtered = players.filter { it.teamId == teamId }
            adapter.submitList(filtered)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}