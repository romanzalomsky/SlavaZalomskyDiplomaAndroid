package com.zalomsky.sportscore.domain.models.responses

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class LeagueResponseModel(
    @SerializedName("id") val id: String,
    @SerializedName("leagueName") val leagueName: String,
    @SerializedName("leagueImage") val leagueImage: String,
    @SerializedName("teams") val leagueTeams: String,
    @SerializedName("countryId") val countryId: String,
    @SerializedName("countryName") val countryName: String
)