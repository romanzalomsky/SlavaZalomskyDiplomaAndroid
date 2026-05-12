package com.zalomsky.sportscore.api

import com.zalomsky.sportscore.domain.models.FavoritePlayerRequest
import com.zalomsky.sportscore.domain.models.PlayerModel
import com.zalomsky.sportscore.domain.models.responses.BaseResponse
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PlayerApi {

    @GET("auth/players")
    suspend fun getPlayers(): List<PlayerResponseModel>

    @POST("auth/players/add")
    suspend fun insertPlayer(@Body player: PlayerModel): Response<PlayerResponseModel>

    @GET("auth/players/search")
    suspend fun searchPlayers(@Query("q") query: String): List<PlayerResponseModel>

    @GET("auth/favorite/players")
    suspend fun getFavoritePlayers(): List<PlayerResponseModel>

    @POST("auth/favorite/players/add")
    suspend fun addFavoritePlayer(@Body request: FavoritePlayerRequest): BaseResponse

    @DELETE("auth/favorite/players/{playerId}")
    suspend fun deleteFavoritePlayer(@Path("playerId") playerId: String): Response<BaseResponse>
}
