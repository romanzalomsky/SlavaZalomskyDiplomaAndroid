package com.zalomsky.sportscore.domain.models.responses

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class MatchResponseModel (

    @SerializedName("matchId") val matchId: String,
    @SerializedName("homeTeam") val homeTeam: TeamResponseModel? = null,
    @SerializedName("awayTeam") val awayTeam: TeamResponseModel? = null,
    @SerializedName("homePlayer") val homePlayer: PlayerResponseModel? = null,
    @SerializedName("awayPlayer") val awayPlayer: PlayerResponseModel? = null,
    @SerializedName("leagueId") val leagueId: String,
    @SerializedName("matchWeek") val matchWeek: Int,
    @SerializedName("matchDate") val matchDate: String? = null,
    @SerializedName("streamUrl") val streamUrl: String? = null,
    @SerializedName("homeScore") val homeScore: Int? = null,
    @SerializedName("awayScore") val awayScore: Int? = null
) {
    val homeName: String
        get() = homeTeam?.teamName ?: homePlayer?.playerName ?: "—"

    val awayName: String
        get() = awayTeam?.teamName ?: awayPlayer?.playerName ?: "—"

    val homeImageUrl: String?
        get() = homeTeam?.teamIcon ?: homePlayer?.playerImage

    val awayImageUrl: String?
        get() = awayTeam?.teamIcon ?: awayPlayer?.playerImage
}

sealed class ScheduleUiState {
    data object Loading : ScheduleUiState()
    data class Success(val matches: List<MatchResponseModel>) : ScheduleUiState()
    data class Error(val message: String) : ScheduleUiState()
}