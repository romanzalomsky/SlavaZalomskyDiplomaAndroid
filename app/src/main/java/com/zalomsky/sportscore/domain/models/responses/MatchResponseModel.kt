package com.zalomsky.sportscore.domain.models.responses

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class MatchResponseModel (

    @SerializedName("matchId")val matchId: String,
    @SerializedName("homeTeam")val homeTeam: TeamResponseModel,
    @SerializedName("awayTeam")val awayTeam: TeamResponseModel,
    @SerializedName("leagueId")val leagueId: String,
    @SerializedName("matchWeek")val matchWeek: Int,
    @SerializedName("matchDate")val matchDate: String? = null,
    @SerializedName("homeScore")val homeScore: Int? = null,
    @SerializedName("awayScore")val awayScore: Int? = null
)