package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.team

import androidx.recyclerview.widget.DiffUtil
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel

class TeamDiffCallBack(
    private val oldList: List<TeamResponseModel>,
    private val newList: List<TeamResponseModel>
): DiffUtil.Callback() {
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
        return oldList[oldItemPosition].teamId == newList[newItemPosition].teamId
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}