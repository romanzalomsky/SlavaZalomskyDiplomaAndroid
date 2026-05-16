package com.zalomsky.sportscore.domain.usecase.player

import com.zalomsky.sportscore.data.LeagueRepositoryImpl
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel
import javax.inject.Inject

class GetPlayersByLeagueIdUseCase @Inject constructor(
    private val repository: LeagueRepositoryImpl
) {
    suspend operator fun invoke(leagueId: String): List<PlayerResponseModel> {
        return repository.getPlayersByLeagueId(leagueId)
    }
}
