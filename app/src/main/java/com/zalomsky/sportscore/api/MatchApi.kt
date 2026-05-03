package com.zalomsky.sportscore.api

import com.zalomsky.sportscore.domain.models.responses.MatchResponseModel
import retrofit2.http.GET
import retrofit2.http.Path

interface MatchApi {

    @GET("auth/league/{leagueId}/schedule")
    suspend fun getScheduleByLeagueId(
        @Path("leagueId") leagueId: String
    ): List<MatchResponseModel>

    @GET("auth/favorites/schedule")
    suspend fun getFavoriteSchedule(): List<MatchResponseModel>
}
