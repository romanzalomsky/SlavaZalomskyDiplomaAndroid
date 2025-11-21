package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.country

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.Country

class CountryAdapter(
    private var countries: List<Country>,
    private val onDeleteClicked: (Country) -> Unit
) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    class CountryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val countryNameText: TextView = view.findViewById(R.id.countryNameText)
        val countryFlagImage: ImageView = view.findViewById(R.id.countryFlagImageView)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteCountryButton)
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

        holder.deleteButton.setOnClickListener {
            onDeleteClicked(country)
        }
    }

    override fun getItemCount(): Int = countries.size

    fun updateList(newCountries: List<Country>) {
        val diffCallback = CountryDiffCallback(this.countries, newCountries)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.countries = newCountries
        diffResult.dispatchUpdatesTo(this)
    }
}
