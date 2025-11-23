package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.city

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
import com.zalomsky.sportscore.domain.models.CityModel
import com.zalomsky.sportscore.domain.models.responses.CityResponseModel

class CityAdapter(
    private var cities: List<CityResponseModel>,
    private val onDeleteClicked: (CityResponseModel) -> Unit
) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    class CityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cityNameText: TextView = view.findViewById(R.id.cityNameText)
        val countryNameTextView: TextView = view.findViewById(R.id.countryNameTextView)
        val cityIconImageView: ImageView = view.findViewById(R.id.cityIconImageView)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteCityButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_city, parent, false)
        return CityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = cities[position]

        holder.cityNameText.text = city.name
        holder.countryNameTextView.text = "(${city.countryName})"

        Glide.with(holder.cityIconImageView.context)
            .load(city.countryFlag)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_pause)
            .into(holder.cityIconImageView)

        holder.deleteButton.setOnClickListener {
            onDeleteClicked(city)
        }
    }

    override fun getItemCount(): Int = cities.size

    fun updateList(newCities: List<CityResponseModel>) {
        val diffCallback = CityDiffCallback(this.cities, newCities)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.cities = newCities
        diffResult.dispatchUpdatesTo(this)
    }
}