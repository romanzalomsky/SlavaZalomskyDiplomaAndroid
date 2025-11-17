package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.league

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.LeagueResponseModel

class LeagueAdapter(
    private var leagues: List<LeagueResponseModel>
) : RecyclerView.Adapter<LeagueAdapter.LeagueViewHolder>() {

    class LeagueViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val leagueImageView: ImageView = view.findViewById(R.id.leagueImageView)
        val leagueNameText: TextView = view.findViewById(R.id.leagueNameText)
        val countryTextView: TextView = view.findViewById(R.id.leagueCountryTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeagueViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_league, parent, false)
        return LeagueViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeagueViewHolder, position: Int) {
        val league = leagues[position]

        holder.leagueNameText.text = league.leagueName
        holder.countryTextView.text = "(${league.countryName})"

        Glide.with(holder.leagueImageView.context)
            .load(league.leagueImage)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_pause)
            .into(holder.leagueImageView)
    }

    override fun getItemCount(): Int = leagues.size

    fun updateList(newLeagues: List<LeagueResponseModel>) {
        val diffCallback = LeagueDiffCallback(this.leagues, newLeagues)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.leagues = newLeagues
        diffResult.dispatchUpdatesTo(this)
    }
}