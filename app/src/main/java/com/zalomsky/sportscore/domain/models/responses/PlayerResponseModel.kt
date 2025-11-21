package com.zalomsky.sportscore.domain.models.responses

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class PlayerResponseModel(
    @SerializedName("playerId") val playerId: String,
    @SerializedName("playerName") val playerName: String,
    @SerializedName("playerImage") val playerImage: String,
    @SerializedName("playerPosition") val playerPosition: String,
    @SerializedName("countryId") val countryId: String,
    @SerializedName("countryName") val countryName: String,
    @SerializedName("countryImage") val countryImage: String
)