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
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PlayerApi {

    @GET("auth/players")
    suspend fun getPlayers(): List<PlayerResponseModel>

    @POST("auth/players/add")
    suspend fun insertPlayer(@Body player: PlayerModel): Response<PlayerResponseModel>

    @GET("auth/players/search")
    suspend fun searchPlayers(@Query("q") query: String): List<PlayerResponseModel>

    @GET("auth/players/searchForLeague")
    suspend fun searchPlayersForLeague(
        @Query("query") query: String,
        @Query("leagueId") leagueId: String
    ): List<PlayerResponseModel>

    @PUT("auth/players/{playerId}/assign")
    suspend fun assignPlayerToLeague(
        @Path("playerId") playerId: String,
        @Body leagueIdWrapper: com.zalomsky.sportscore.domain.models.LeagueIdWrapper
    ): Response<Unit>

    @GET("auth/favorite/players")
    suspend fun getFavoritePlayers(): List<PlayerResponseModel>

    @POST("auth/favorite/players/add")
    suspend fun addFavoritePlayer(@Body request: FavoritePlayerRequest): BaseResponse

    @DELETE("auth/favorite/players/{playerId}")
    suspend fun deleteFavoritePlayer(@Path("playerId") playerId: String): Response<BaseResponse>
}
