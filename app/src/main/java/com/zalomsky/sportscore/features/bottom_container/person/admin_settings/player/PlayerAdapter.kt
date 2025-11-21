package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.player

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

class PlayerAdapter(
    private var players: List<PlayerResponseModel>
): RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerImageView: ImageView = view.findViewById(R.id.playerImageView)
        val playerNameText: TextView = view.findViewById(R.id.playerNameText)
        val playerPositionText: TextView = view.findViewById(R.id.playerPositionText)
        val playerNationalityText: TextView = view.findViewById(R.id.playerNationalityText)
        val playerCountryFlagView: ImageView = view.findViewById(R.id.playerCountryFlagView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlayerViewHolder,
        position: Int
    ) {
        val player = players[position]

        holder.playerNameText.text = player.playerName
        holder.playerPositionText.text = player.playerPosition
        holder.playerNationalityText.text = "(${player.countryName})"

        Glide.with(holder.playerImageView.context)
            .load(player.playerImage)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_pause)
            .into(holder.playerImageView)

        Glide.with(holder.playerCountryFlagView.context)
            .load(player.countryImage)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_pause)
            .into(holder.playerCountryFlagView)
    }

    override fun getItemCount(): Int = players.size

    fun updateList(newPlayers: List<PlayerResponseModel>) {
        val diffCallback = PlayerDiffCallback(this.players, newPlayers)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.players = newPlayers
        diffResult.dispatchUpdatesTo(this)
    }
}