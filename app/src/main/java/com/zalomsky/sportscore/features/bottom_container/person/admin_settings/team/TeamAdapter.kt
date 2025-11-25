package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.team

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.player.PlayerAdapter.PlayerViewHolder
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.player.PlayerDiffCallback

class TeamAdapter(
    private var teams: List<TeamResponseModel>
): RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    class TeamViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val teamNameText: TextView = view.findViewById(R.id.teamNameTextView)
        val teamIconImage: ImageView = view.findViewById(R.id.teamIconImageView)
        val countryFlagImage: ImageView = view.findViewById(R.id.countryFlagImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_team, parent, false)
        return TeamViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TeamViewHolder,
        position: Int
    ) {
        val team = teams[position]
        holder.teamNameText.text = team.teamName

        Glide.with(holder.teamIconImage.context)
            .load(team.teamIcon)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_pause)
            .into(holder.teamIconImage)

        Glide.with(holder.countryFlagImage.context)
            .load(team.countryImage)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_pause)
            .into(holder.countryFlagImage)
    }

    override fun getItemCount(): Int = teams.size

    fun updateList(newTeams: List<TeamResponseModel>) {
        val diffCallback = TeamDiffCallBack(this.teams, newTeams)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.teams = newTeams
        diffResult.dispatchUpdatesTo(this)
    }


}