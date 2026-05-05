package com.zalomsky.sportscore.features.bottom_container.leagues

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel

class PlayersAdapter : RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder>() {

    private var players: List<PlayerResponseModel> = emptyList()

    fun submitList(newList: List<PlayerResponseModel>) {
        players = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(players[position])
    }

    override fun getItemCount(): Int = players.size

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.playerImageView)
        private val name: TextView = itemView.findViewById(R.id.playerNameText)
        private val pos: TextView = itemView.findViewById(R.id.playerPositionText)

        fun bind(player: PlayerResponseModel) {
            name.text = player.playerName
            pos.text = player.playerPosition

            Glide.with(itemView.context)
                .load(player.playerImage)
                .circleCrop()
                .into(icon)
        }
    }
}