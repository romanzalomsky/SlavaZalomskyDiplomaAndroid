package com.zalomsky.sportscore.domain.usecase.player

import com.zalomsky.sportscore.data.PlayerRepositoryImpl
import retrofit2.Response
import javax.inject.Inject

class AssignPlayerToLeagueUseCase @Inject constructor(
    private val repository: PlayerRepositoryImpl
) {
    suspend operator fun invoke(playerId: String, leagueId: String): Response<Unit> {
        return repository.assignPlayerToLeague(playerId, leagueId)
    }
}
