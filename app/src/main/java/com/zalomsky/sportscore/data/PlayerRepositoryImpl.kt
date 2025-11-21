package com.zalomsky.sportscore.data

import com.zalomsky.sportscore.api.PlayerApi
import com.zalomsky.sportscore.domain.models.PlayerModel
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val playerApi: PlayerApi
) {

    suspend fun getPlayers(): List<PlayerResponseModel> = playerApi.getPlayers()

    suspend fun insertPlayer(player: PlayerModel) = playerApi.insertPlayer(player)
}