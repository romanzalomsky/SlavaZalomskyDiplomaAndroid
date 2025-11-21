package com.zalomsky.sportscore.domain.usecase.player

import com.zalomsky.sportscore.data.PlayerRepositoryImpl
import com.zalomsky.sportscore.domain.models.PlayerModel
import javax.inject.Inject

class InsertPlayerUseCase @Inject constructor(
    private val playerRepositoryImpl: PlayerRepositoryImpl
) {

    suspend operator fun invoke(player: PlayerModel) = playerRepositoryImpl.insertPlayer(player)
}