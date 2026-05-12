package com.zalomsky.sportscore.domain.usecase.player

import com.zalomsky.sportscore.data.PlayerRepositoryImpl
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel
import javax.inject.Inject

class SearchPlayersUseCase @Inject constructor(
    private val playerRepositoryImpl: PlayerRepositoryImpl
) {
    suspend operator fun invoke(query: String): List<PlayerResponseModel> = playerRepositoryImpl.searchPlayers(query)
}
