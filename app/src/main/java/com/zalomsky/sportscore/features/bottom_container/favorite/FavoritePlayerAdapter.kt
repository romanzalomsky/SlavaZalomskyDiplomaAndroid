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
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel

class FavoritePlayerAdapter(
    private var players: List<PlayerResponseModel>,
    private val onItemClick: (PlayerResponseModel) -> Unit,
    private val onDeleteClick: (String) -> Unit
): RecyclerView.Adapter<FavoritePlayerAdapter.PlayerFavoriteViewHolder>() {

    class PlayerFavoriteViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val playerNameText: TextView = view.findViewById(R.id.teamFavoriteNameTextView)
        val playerIconImage: ImageView = view.findViewById(R.id.teamFavoriteIconImageView)
        val playerFavoriteDeleteIcon: ImageView = view.findViewById(R.id.teamFavoriteDeleteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerFavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite, parent, false)
        return PlayerFavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerFavoriteViewHolder, position: Int) {
        val player = players[position]
        holder.playerNameText.text = player.playerName

        holder.itemView.setOnClickListener {
            onItemClick(player)
        }

        holder.playerFavoriteDeleteIcon.setOnClickListener {
            onDeleteClick(player.playerId)
        }

        Glide.with(holder.playerIconImage.context)
            .load(player.playerImage)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_pause)
            .into(holder.playerIconImage)
    }

    override fun getItemCount(): Int = players.size

    fun updateList(newPlayers: List<PlayerResponseModel>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = players.size
            override fun getNewListSize(): Int = newPlayers.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                players[oldItemPosition].playerId == newPlayers[newItemPosition].playerId
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                players[oldItemPosition] == newPlayers[newItemPosition]
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.players = newPlayers
        diffResult.dispatchUpdatesTo(this)
    }
}
