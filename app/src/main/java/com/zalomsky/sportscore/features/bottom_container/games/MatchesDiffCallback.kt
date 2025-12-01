package com.zalomsky.sportscore.features.bottom_container.games

import androidx.recyclerview.widget.DiffUtil
import com.zalomsky.sportscore.domain.models.responses.MatchResponseModel

class MatchesDiffCallback : DiffUtil.ItemCallback<MatchResponseModel>() {
    override fun areItemsTheSame(oldItem: MatchResponseModel, newItem: MatchResponseModel): Boolean {
        return oldItem.matchId == newItem.matchId
    }

    override fun areContentsTheSame(oldItem: MatchResponseModel, newItem: MatchResponseModel): Boolean {
        return oldItem == newItem
    }
}