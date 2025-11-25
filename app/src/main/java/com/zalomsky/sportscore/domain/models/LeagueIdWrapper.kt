package com.zalomsky.sportscore.domain.models

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@Serializable
@SuppressLint("UnsafeOptInUsageError")
data class LeagueIdWrapper(
    val leagueId: String
)
