package com.zalomsky.sportscore.features.bottom_container.favorite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.team.TeamDiffCallBack

class FavoriteTeamAdapter(
    private var teams: List<TeamResponseModel>,
    private val onItemClick: (TeamResponseModel) -> Unit,
    private val onDeleteClick: (String) -> Unit
): RecyclerView.Adapter<FavoriteTeamAdapter.TeamFavoriteViewHolder>() {

    class TeamFavoriteViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val teamNameText: TextView = view.findViewById(R.id.teamFavoriteNameTextView)
        val teamIconImage: ImageView = view.findViewById(R.id.teamFavoriteIconImageView)
        val teamFavoriteDeleteIcon: ImageView = view.findViewById(R.id.teamFavoriteDeleteIcon)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteTeamAdapter.TeamFavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite, parent, false)
        return TeamFavoriteViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: FavoriteTeamAdapter.TeamFavoriteViewHolder,
        position: Int
    ) {
        val team = teams[position]
        holder.teamNameText.text = team.teamName

        holder.itemView.setOnClickListener {
            onItemClick(team)
        }

        holder.teamFavoriteDeleteIcon.setOnClickListener {
            onDeleteClick(team.teamId)
        }

        Glide.with(holder.teamIconImage.context)
            .load(team.teamIcon)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_pause)
            .into(holder.teamIconImage)
    }

    override fun getItemCount(): Int = teams.size

    fun updateList(newTeams: List<TeamResponseModel>) {
        val diffCallback = TeamDiffCallBack(this.teams, newTeams)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.teams = newTeams
        diffResult.dispatchUpdatesTo(this)
    }
}