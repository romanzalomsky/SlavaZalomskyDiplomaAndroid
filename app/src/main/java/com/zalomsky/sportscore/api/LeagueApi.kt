package com.zalomsky.sportscore.api

import com.zalomsky.sportscore.domain.models.LeagueModel
import com.zalomsky.sportscore.domain.models.responses.LeagueResponseModel
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface LeagueApi {
    @GET("/auth/leagues")
    suspend fun getLeagues(): List<LeagueResponseModel>

    @POST("/auth/leagues/add")
    suspend fun insertLeague(@Body league: LeagueModel): Response<LeagueResponseModel>

    @GET("/auth/leagues/{leagueId}")
    suspend fun getLeagueById(@Path("leagueId") leagueId: String): LeagueResponseModel

    @PUT("/auth/leagues/{leagueId}")
    suspend fun updateLeague(
        @Path("leagueId") leagueId: String,
        @Body league: LeagueModel
    ): Response<LeagueResponseModel>

    @GET("/auth/leagues/{leagueId}/teams")
    suspend fun getTeamsByLeagueId(@Path("leagueId") leagueId: String): List<TeamResponseModel>
}