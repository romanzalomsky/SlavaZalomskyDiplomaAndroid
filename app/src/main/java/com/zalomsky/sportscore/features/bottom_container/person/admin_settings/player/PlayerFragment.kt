package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.Country
import com.zalomsky.sportscore.domain.models.LeagueModel
import com.zalomsky.sportscore.domain.models.PlayerModel
import com.zalomsky.sportscore.domain.models.TeamModel
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.league.LeagueAdapter
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.league.LeagueViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class PlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModels()
    private lateinit var playerAdapter: PlayerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.playersRecyclerView)

        playerAdapter = PlayerAdapter(emptyList())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playerAdapter
        }

        viewModel.getTeamsList()

        viewModel.players.observe(viewLifecycleOwner) { playersList ->
            playersList?.let {
                playerAdapter.updateList(it)
            }
        }

        if (viewModel.players.value.isNullOrEmpty()) {
            viewModel.getPlayersList()
        }
    }
}