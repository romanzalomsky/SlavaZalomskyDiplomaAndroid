package com.zalomsky.sportscore.domain.models

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class PlayerModel(
    @SerializedName("playerId") val playerId: String,
    @SerializedName("playerName") val playerName: String,
    @SerializedName("playerImage") val playerImage: String,
    @SerializedName("playerPosition") val playerPosition: String,
    @SerializedName("teamId") val teamId: String,
)
