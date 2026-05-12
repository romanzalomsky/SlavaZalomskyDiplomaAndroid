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

class FavoriteSearchPlayerAdapter(
    private var players: List<PlayerResponseModel>,
    private val onFavoriteClick: (PlayerResponseModel) -> Unit
): RecyclerView.Adapter<FavoriteSearchPlayerAdapter.PlayerSimpleViewHolder>() {

    class PlayerSimpleViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val playerNameText: TextView = view.findViewById(R.id.teamSimpleNameTextView)
        val playerIconImage: ImageView = view.findViewById(R.id.teamSimpleIconImageView)
        val favoriteIcon: View = view.findViewById(R.id.btnAddToFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerSimpleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_team_search_simple, parent, false)
        return PlayerSimpleViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerSimpleViewHolder, position: Int) {
        val player = players[position]
        holder.playerNameText.text = player.playerName

        Glide.with(holder.playerIconImage.context)
            .load(player.playerImage)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_pause)
            .into(holder.playerIconImage)

        holder.favoriteIcon.setOnClickListener {
            onFavoriteClick(player)
        }
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
