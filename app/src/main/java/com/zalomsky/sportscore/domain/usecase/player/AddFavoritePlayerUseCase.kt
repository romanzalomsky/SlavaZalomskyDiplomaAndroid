package com.zalomsky.sportscore.domain.usecase.player

import com.zalomsky.sportscore.data.PlayerRepositoryImpl
import com.zalomsky.sportscore.domain.models.responses.BaseResponse
import javax.inject.Inject

class AddFavoritePlayerUseCase @Inject constructor(
    private val playerRepositoryImpl: PlayerRepositoryImpl
) {
    suspend operator fun invoke(playerId: String): BaseResponse = playerRepositoryImpl.addFavoritePlayer(playerId)
}
