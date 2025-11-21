package com.zalomsky.sportscore.domain.models

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class LeagueModel(
    @SerializedName("id") val id: String,
    @SerializedName("leagueName") val leagueName: String,
    @SerializedName("leagueImage") val leagueImage: String,
    @SerializedName("teams") val leagueTeams: String,
    @SerializedName("countryId") val countryId: String
)
