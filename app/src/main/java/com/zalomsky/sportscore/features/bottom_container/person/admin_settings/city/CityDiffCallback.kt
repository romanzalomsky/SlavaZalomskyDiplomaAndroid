package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.city

import androidx.recyclerview.widget.DiffUtil
import com.zalomsky.sportscore.domain.models.responses.CityResponseModel

class CityDiffCallback(
    private val oldList: List<CityResponseModel>,
    private val newList: List<CityResponseModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}