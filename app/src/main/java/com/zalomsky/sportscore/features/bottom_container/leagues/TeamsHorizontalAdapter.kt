package com.zalomsky.sportscore.features.bottom_container.leagues

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel

class TeamsHorizontalAdapter(
    private val onTeamClick: (TeamResponseModel) -> Unit
) : RecyclerView.Adapter<TeamsHorizontalAdapter.TeamViewHolder>() {

    private var teams: List<TeamResponseModel> = emptyList()

    fun submitList(newList: List<TeamResponseModel>) {
        teams = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_team_horizontal, parent, false)
        return TeamViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(teams[position])
    }

    override fun getItemCount(): Int = teams.size

    inner class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.teamIcon)
        private val name: TextView = itemView.findViewById(R.id.teamName)
        private val country: TextView = itemView.findViewById(R.id.teamCountry)
        private val btnDetails: Button = itemView.findViewById(R.id.btnDetails)

        fun bind(team: TeamResponseModel) {
            name.text = team.teamName
            country.text = team.countryName

            Glide.with(itemView.context)
                .load(team.teamIcon)
                .into(icon)

            val clickListener = View.OnClickListener { onTeamClick(team) }
            itemView.setOnClickListener(clickListener)
            btnDetails.setOnClickListener(clickListener)
        }
    }
}