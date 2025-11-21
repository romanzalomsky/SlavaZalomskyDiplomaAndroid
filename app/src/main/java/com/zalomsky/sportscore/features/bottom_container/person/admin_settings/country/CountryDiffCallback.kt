package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.country

import androidx.recyclerview.widget.DiffUtil
import com.zalomsky.sportscore.domain.models.Country

class CountryDiffCallback(
    private val oldList: List<Country>,
    private val newList: List<Country>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}