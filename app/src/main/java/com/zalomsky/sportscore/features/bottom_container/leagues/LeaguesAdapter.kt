package com.zalomsky.sportscore.features.bottom_container.leagues

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.LeagueResponseModel

class LeaguesAdapter(
    private val onLeagueClick: (LeagueResponseModel) -> Unit
) : RecyclerView.Adapter<LeaguesAdapter.LeagueViewHolder>() {

    private var leagues: List<LeagueResponseModel> = emptyList()

    fun submitList(newList: List<LeagueResponseModel>) {
        leagues = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeagueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_league, parent, false)
        return LeagueViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeagueViewHolder, position: Int) {
        holder.bind(leagues[position])
    }

    override fun getItemCount(): Int = leagues.size

    inner class LeagueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.leagueIcon)
        private val name: TextView = itemView.findViewById(R.id.leagueName)
        private val country: TextView = itemView.findViewById(R.id.countryName)
        private val teamCount: TextView = itemView.findViewById(R.id.teamCountText)

        fun bind(league: LeagueResponseModel) {
            name.text = league.leagueName
            country.text = league.countryName
            teamCount.text = itemView.context.getString(R.string.team_count_format, league.leagueTeams.size)

            Glide.with(itemView.context)
                .load(league.leagueImage)
                .into(icon)

            itemView.setOnClickListener { onLeagueClick(league) }
        }
    }
}