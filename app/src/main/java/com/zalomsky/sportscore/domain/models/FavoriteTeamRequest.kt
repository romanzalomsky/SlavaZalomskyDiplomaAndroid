package com.zalomsky.sportscore.domain.models

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class FavoriteTeamRequest(
    val teamId: String
)
