package com.zalomsky.sportscore.features.bottom_container.person.user_settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.player.PlayerAdapter
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.team.TeamViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class TeamDetailFragment : Fragment() {

    private val args: TeamDetailFragmentArgs by navArgs()
    private val viewModel: TeamViewModel by viewModels()
    private lateinit var playerAdapter: PlayerAdapter

    private lateinit var teamIconImage: ImageView
    private lateinit var detailTeamNameText: TextView
    private lateinit var detailTeamCountryFlag: ImageView
    private lateinit var detailTeamCountryValue: TextView
    private lateinit var detailTeamCoachValue: TextView
    private lateinit var detailTeamStadiumValue: TextView
    private lateinit var detailTeamFoundationDateValue: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view = inflater.inflate(R.layout.fragment_team_detail, container, false)

        initializeViews(view)

        val teamId = args.teamId

        if (teamId != null) {
            viewModel.getTeamById(teamId)
        }

        observeViewModel()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.players_detail_recycler_view)
        playerAdapter = PlayerAdapter(emptyList())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playerAdapter
        }

        val teamId = args.teamId

        if (teamId != null) {
            viewModel.getPlayersByTeamId(teamId)
        }

        viewModel.players.observe(viewLifecycleOwner) { playersList ->
            playersList?.let {
                playerAdapter.updateList(it)
            }
        }
    }

    private fun initializeViews(view: View) {
        teamIconImage = view.findViewById(R.id.team_icon_image)
        detailTeamNameText = view.findViewById(R.id.detail_team_name_text)
        detailTeamCountryFlag = view.findViewById(R.id.detail_team_country_flag)
        detailTeamCountryValue = view.findViewById(R.id.detail_team_country_value)
        detailTeamCoachValue = view.findViewById(R.id.detail_team_coach_value)
        detailTeamStadiumValue = view.findViewById(R.id.detail_team_stadium_value)
        detailTeamFoundationDateValue = view.findViewById(R.id.detail_team_foundation_date_value)
    }

    private fun observeViewModel() {
        viewModel.team.observe(viewLifecycleOwner) { team ->
            team?.let {
                updateUi(it)
            }
        }
    }

    private fun updateUi(team: TeamResponseModel) {

        Glide.with(this)
            .load(team.teamIcon)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_pause)
            .into(teamIconImage)

        detailTeamNameText.text = team.teamName

        detailTeamCountryValue.text = team.countryName
        Glide.with(this)
            .load(team.countryImage)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_pause)
            .into(detailTeamCountryFlag)

        detailTeamCoachValue.text = team.teamCoach
        detailTeamStadiumValue.text = team.teamStadium
        detailTeamFoundationDateValue.text = team.dateOfFoundation
    }
}