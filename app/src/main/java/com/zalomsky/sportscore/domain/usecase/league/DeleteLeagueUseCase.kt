package com.zalomsky.sportscore.domain.usecase.league

import com.zalomsky.sportscore.data.LeagueRepositoryImpl
import javax.inject.Inject

class DeleteLeagueUseCase @Inject constructor(
    private val leagueRepositoryImpl: LeagueRepositoryImpl
) {
    suspend operator fun invoke(leagueId: String) = leagueRepositoryImpl.deleteLeague(leagueId)
}
