package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.country

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.Country

class CountryAdapter(
    private var countries: List<Country>
) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    class CountryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val countryNameText: TextView = view.findViewById(R.id.countryNameText)
        val countryFlagImage: ImageView = view.findViewById(R.id.countryFlagImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_country, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = countries[position]

        holder.countryNameText.text = country.name

        Glide.with(holder.countryFlagImage.context)
            .load(country.flag)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_pause)
            .into(holder.countryFlagImage)
    }

    override fun getItemCount(): Int = countries.size

    fun updateList(newCountries: List<Country>) {
        val diffCallback = CountryDiffCallback(this.countries, newCountries)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.countries = newCountries
        diffResult.dispatchUpdatesTo(this)
    }
}

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