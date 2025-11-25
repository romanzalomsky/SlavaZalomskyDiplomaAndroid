package com.zalomsky.sportscore.domain.models.responses

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class TeamResponseModel(

    @SerializedName("teamId") val teamId: String,
    @SerializedName("teamName") val teamName: String,
    @SerializedName("teamIcon") val teamIcon: String,
    @SerializedName("dateOfFoundation") val dateOfFoundation: String,
    @SerializedName("teamCoach") val teamCoach: String,
    @SerializedName("teamStadium") val teamStadium: String,
    @SerializedName("cityId") val cityId: String,
    @SerializedName("countryId") val countryId: String,
    @SerializedName("countryName") val countryName: String,
    @SerializedName("countryImage") val countryImage: String
)
