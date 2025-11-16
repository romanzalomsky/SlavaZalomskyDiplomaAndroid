package com.zalomsky.sportscore.domain.models

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class CityModel(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("countryId") val countryId: String
)
