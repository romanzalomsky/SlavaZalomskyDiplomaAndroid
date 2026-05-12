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

typealias OnFavoriteClickListener = (team: TeamResponseModel) -> Unit

class FavoriteSearchTeamAdapter(
    private var teams: List<TeamResponseModel>,
    private val onFavoriteClick: OnFavoriteClickListener
): RecyclerView.Adapter<FavoriteSearchTeamAdapter.TeamSimpleViewHolder>(){

    class TeamSimpleViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val teamNameText: TextView = view.findViewById(R.id.teamSimpleNameTextView)
        val teamIconImage: ImageView = view.findViewById(R.id.teamSimpleIconImageView)
        val favoriteIcon: View = view.findViewById(R.id.btnAddToFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamSimpleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_team_search_simple, parent, false)
        return TeamSimpleViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: FavoriteSearchTeamAdapter.TeamSimpleViewHolder,
        position: Int
    ) {
        val team = teams[position]
        holder.teamNameText.text = team.teamName

        Glide.with(holder.teamIconImage.context)
            .load(team.teamIcon)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_pause)
            .into(holder.teamIconImage)
        holder.favoriteIcon.setOnClickListener {

            onFavoriteClick(team)
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