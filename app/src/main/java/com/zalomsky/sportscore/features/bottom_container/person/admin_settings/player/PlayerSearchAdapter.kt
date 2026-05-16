package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel

class PlayerSearchAdapter(
    private var players: List<PlayerResponseModel>,
    private val onPlayerAddClick: (playerId: String) -> Unit
) : RecyclerView.Adapter<PlayerSearchAdapter.PlayerSearchViewHolder>() {

    class PlayerSearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerName: TextView = view.findViewById(R.id.teamNameText)
        val playerPosition: TextView = view.findViewById(R.id.teamCountryText)
        val addButton: Button = view.findViewById(R.id.addButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerSearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_team_search_result, parent, false)
        return PlayerSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerSearchViewHolder, position: Int) {
        val player = players[position]
        holder.playerName.text = player.playerName
        holder.playerPosition.text = "(${player.playerPosition})"
        holder.addButton.setOnClickListener {
            onPlayerAddClick(player.playerId)
        }
    }

    override fun getItemCount(): Int = players.size

    fun updateList(newPlayers: List<PlayerResponseModel>) {
        val diffCallback = PlayerDiffCallback(this.players, newPlayers)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.players = newPlayers
        diffResult.dispatchUpdatesTo(this)
    }
}
