package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.team

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel

class LeagueTeamsAdapter(
    private var teams: List<TeamResponseModel>,
    private val onTeamRemoveClick: (teamId: String) -> Unit = {}
) : RecyclerView.Adapter<LeagueTeamsAdapter.LeagueTeamViewHolder>() {

    class LeagueTeamViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val teamName: TextView = view.findViewById(R.id.teamNameText)
        val removeButton: ImageButton = view.findViewById(R.id.removeTeamButton)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LeagueTeamViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_team_small, parent, false)
        return LeagueTeamViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeagueTeamViewHolder, position: Int) {
        val team = teams[position]

        holder.teamName.text = team.teamName

        holder.removeButton.setOnClickListener {
            onTeamRemoveClick(team.teamId)
        }
    }

    override fun getItemCount(): Int = teams.size

    fun updateList(newTeams: List<TeamResponseModel>) {
        val diffCallback = TeamDiffCallBack(this.teams, newTeams)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.teams = newTeams
        diffResult.dispatchUpdatesTo(this)
    }
}