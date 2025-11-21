package com.zalomsky.sportscore.api

import com.zalomsky.sportscore.domain.models.TeamModel
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TeamApi {

    @GET("/auth/teams")
    suspend fun getTeams(): List<TeamResponseModel>

    @POST("/auth/teams/add")
    suspend fun insertTeam(@Body team: TeamModel): Response<TeamResponseModel>
}