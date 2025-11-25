package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.team

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel

class TeamSearchAdapter(
    private var teams: List<TeamResponseModel>,
    private val onTeamAddClick: (teamId: String) -> Unit
) : RecyclerView.Adapter<TeamSearchAdapter.TeamSearchViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TeamSearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_team_search_result, parent, false)
        return TeamSearchViewHolder(view)
    }

    class TeamSearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val teamName: TextView = view.findViewById(R.id.teamNameText)
        val teamCountry: TextView = view.findViewById(R.id.teamCountryText)
        val addButton: Button = view.findViewById(R.id.addButton)
    }

    override fun onBindViewHolder(holder: TeamSearchViewHolder, position: Int) {
        val team = teams[position]
        holder.teamName.text = team.teamName
        holder.teamCountry.text = "(${team.countryName})"

        holder.addButton.setOnClickListener {
            onTeamAddClick(team.teamId)
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