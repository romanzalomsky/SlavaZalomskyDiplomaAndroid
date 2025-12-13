package com.zalomsky.sportscore.api

import androidx.room.Delete
import com.zalomsky.sportscore.domain.models.FavoriteTeamRequest
import com.zalomsky.sportscore.domain.models.LeagueIdWrapper
import com.zalomsky.sportscore.domain.models.TeamModel
import com.zalomsky.sportscore.domain.models.responses.BaseResponse
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TeamApi {

    @GET("/auth/teams")
    suspend fun getTeams(): List<TeamResponseModel>

    @POST("/auth/teams/add")
    suspend fun insertTeam(@Body team: TeamModel): Response<TeamResponseModel>

    @GET("/auth/teams/{teamId}")
    suspend fun getTeamById(@Query("teamId") teamId: String): TeamResponseModel?

    @GET("/auth/teams/search")
    suspend fun searchTeams(
        @Query("query") query: String,
        @Query("leagueId") leagueId: String
    ): List<TeamResponseModel>

    @GET("/auth/teams/searchByName")
    suspend fun searchTeamsSimple(
        @Query("query") query: String
    ): List<TeamResponseModel>

    @PUT("/auth/teams/{teamId}/assign")
    suspend fun assignTeamToLeague(
        @Path("teamId") teamId: String,
        @Body leagueIdWrapper: LeagueIdWrapper
    ): Response<Unit>

    @POST("/auth/favorites/add")
    suspend fun addFavoriteTeam(@Body request: FavoriteTeamRequest): BaseResponse

    @DELETE("/auth/favorites/delete/{teamId}")
    suspend fun deleteFromFavorite(@Path("teamId") teamId: String): Response<BaseResponse>

    @GET("/auth/favorites")
    suspend fun getFavoriteTeams(): List<TeamResponseModel>
}