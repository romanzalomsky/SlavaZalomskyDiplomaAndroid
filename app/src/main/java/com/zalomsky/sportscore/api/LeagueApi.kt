package com.zalomsky.sportscore.api

import com.zalomsky.sportscore.domain.models.LeagueModel
import com.zalomsky.sportscore.domain.models.LeagueResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LeagueApi {
    @GET("/auth/leagues")
    suspend fun getLeagues(): List<LeagueResponseModel>

    @POST("/auth/leagues/add")
    suspend fun insertLeague(@Body league: LeagueModel): Response<LeagueResponseModel>
}