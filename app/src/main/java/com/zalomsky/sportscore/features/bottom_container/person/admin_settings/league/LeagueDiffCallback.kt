package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.league

import androidx.recyclerview.widget.DiffUtil
import com.zalomsky.sportscore.domain.models.responses.LeagueResponseModel

class LeagueDiffCallback(
    private val oldList: List<LeagueResponseModel>,
    private val newList: List<LeagueResponseModel>
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
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}