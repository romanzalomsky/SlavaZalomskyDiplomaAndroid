package com.zalomsky.sportscore.data

import com.zalomsky.sportscore.api.PlayerApi
import com.zalomsky.sportscore.domain.models.FavoritePlayerRequest
import com.zalomsky.sportscore.domain.models.PlayerModel
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val playerApi: PlayerApi
) {

    suspend fun getPlayers(): List<PlayerResponseModel> = playerApi.getPlayers()

    suspend fun searchPlayers(query: String): List<PlayerResponseModel> = playerApi.searchPlayers(query)

    suspend fun getFavoritePlayers(): List<PlayerResponseModel> = playerApi.getFavoritePlayers()

    suspend fun addFavoritePlayer(playerId: String) = playerApi.addFavoritePlayer(FavoritePlayerRequest(playerId))

    suspend fun deleteFavoritePlayer(playerId: String) = playerApi.deleteFavoritePlayer(playerId)

    suspend fun insertPlayer(player: PlayerModel) = playerApi.insertPlayer(player)

    suspend fun searchPlayersForLeague(query: String, leagueId: String): List<PlayerResponseModel> {
        return playerApi.searchPlayers(query)
    }

    suspend fun assignPlayerToLeague(playerId: String, leagueId: String): retrofit2.Response<Unit> {
        val wrapper = com.zalomsky.sportscore.domain.models.LeagueIdWrapper(leagueId = leagueId)
        return playerApi.assignPlayerToLeague(playerId, wrapper)
    }
}