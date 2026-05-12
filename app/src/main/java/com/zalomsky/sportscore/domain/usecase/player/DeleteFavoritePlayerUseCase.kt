package com.zalomsky.sportscore.domain.usecase.player

import com.zalomsky.sportscore.data.PlayerRepositoryImpl
import retrofit2.Response
import com.zalomsky.sportscore.domain.models.responses.BaseResponse
import javax.inject.Inject

class DeleteFavoritePlayerUseCase @Inject constructor(
    private val playerRepositoryImpl: PlayerRepositoryImpl
) {
    suspend operator fun invoke(playerId: String): Response<BaseResponse> = playerRepositoryImpl.deleteFavoritePlayer(playerId)
}
