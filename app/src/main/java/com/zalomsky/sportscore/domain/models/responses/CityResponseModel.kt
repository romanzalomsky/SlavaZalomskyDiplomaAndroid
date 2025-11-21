package com.zalomsky.sportscore.domain.models.responses

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class CityResponseModel(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("countryId") val countryId: String,
    @SerializedName("countryName") val countryName: String
)