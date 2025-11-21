package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.team

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.player.PlayerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamFragment : Fragment() {

    private val viewModel: TeamViewModel by viewModels()
    private lateinit var teamAdapter: TeamAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_team, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.teamsRecyclerView)

        teamAdapter = TeamAdapter(emptyList())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = teamAdapter
        }

        viewModel.teams.observe(viewLifecycleOwner) { teamsList ->
            teamsList?.let {
                teamAdapter.updateList(it)
            }
        }

        if (viewModel.teams.value.isNullOrEmpty()) {
            viewModel.getTeamsList()
        }
    }
}