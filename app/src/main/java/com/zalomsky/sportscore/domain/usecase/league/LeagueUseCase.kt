package com.zalomsky.sportscore.domain.usecase.league

import com.zalomsky.sportscore.data.LeagueRepositoryImpl
import com.zalomsky.sportscore.domain.models.responses.LeagueResponseModel
import javax.inject.Inject

class LeagueUseCase @Inject constructor(
    private val leagueRepositoryImpl: LeagueRepositoryImpl
) {
    suspend operator fun invoke(): Result<List<LeagueResponseModel>> = runCatching {
        leagueRepositoryImpl.getLeagues()
    }
}