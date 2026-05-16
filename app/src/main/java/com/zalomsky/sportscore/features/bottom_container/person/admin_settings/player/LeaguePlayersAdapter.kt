package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel

class LeaguePlayersAdapter(
    private var players: List<PlayerResponseModel>,
    private val onPlayerRemoveClick: (playerId: String) -> Unit = {}
) : RecyclerView.Adapter<LeaguePlayersAdapter.LeaguePlayerViewHolder>() {

    class LeaguePlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val playerName: TextView = view.findViewById(R.id.teamNameText)
        val removeButton: ImageButton = view.findViewById(R.id.removeTeamButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaguePlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_team_small, parent, false)
        return LeaguePlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaguePlayerViewHolder, position: Int) {
        val player = players[position]
        holder.playerName.text = player.playerName
        holder.removeButton.setOnClickListener {
            onPlayerRemoveClick(player.playerId)
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
