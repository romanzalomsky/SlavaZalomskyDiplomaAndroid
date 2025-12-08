package com.zalomsky.sportscore.features.bottom_container.person.user_settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel

class TeamDetailAdapter(
    private var players: List<PlayerResponseModel>
): RecyclerView.Adapter<TeamDetailAdapter.PlayerDetailViewHolder>() {

    class PlayerDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerImageView: ImageView = view.findViewById(R.id.playerImageView)
        val playerNameText: TextView = view.findViewById(R.id.playerNameText)
        val playerPositionText: TextView = view.findViewById(R.id.playerPositionText)
        val playerTeamText: TextView = view.findViewById(R.id.playerTeamText)
        val playerTeamFlagView: ImageView = view.findViewById(R.id.playerTeamFlagView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player, parent, false)
        return PlayerDetailViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlayerDetailViewHolder,
        position: Int
    ) {
        val player = players[position]

        holder.playerNameText.text = player.playerName
        holder.playerPositionText.text = player.playerPosition
        holder.playerTeamText.text = player.teamName

        Glide.with(holder.playerImageView.context)
            .load(player.playerImage)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_pause)
            .into(holder.playerImageView)

        Glide.with(holder.playerTeamFlagView.context)
            .load(player.teamImage)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_pause)
            .into(holder.playerTeamFlagView)
    }

    override fun getItemCount(): Int = players.size
}