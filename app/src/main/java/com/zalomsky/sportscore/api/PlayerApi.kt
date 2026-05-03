package com.zalomsky.sportscore.api

import com.zalomsky.sportscore.domain.models.PlayerModel
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PlayerApi {

    @GET("auth/players")
    suspend fun getPlayers(): List<PlayerResponseModel>

    @POST("auth/players/add")
    suspend fun insertPlayer(@Body player: PlayerModel): Response<PlayerResponseModel>
}
