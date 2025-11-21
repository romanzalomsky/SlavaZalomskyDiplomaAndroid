package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.player

import androidx.recyclerview.widget.DiffUtil
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel

class PlayerDiffCallback(
    private val oldList: List<PlayerResponseModel>,
    private val newList: List<PlayerResponseModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldList[oldItemPosition].playerId == newList[newItemPosition].playerId
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}