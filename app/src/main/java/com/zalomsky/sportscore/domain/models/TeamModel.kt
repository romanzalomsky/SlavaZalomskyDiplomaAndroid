package com.zalomsky.sportscore.domain.models

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class TeamModel(

    @SerializedName("teamId") val teamId: String,
    @SerializedName("teamName") val teamName: String,
    @SerializedName("teamIcon") val teamIcon: String,
    @SerializedName("players") val players: String,
    @SerializedName("dateOfFoundation") val dateOfFoundation: String,
    @SerializedName("teamCoach") val teamCoach: String,
    @SerializedName("teamStadium") val teamStadium: String,
    @SerializedName("cityId") val cityId: String,
    @SerializedName("countryId") val countryId: String
)
