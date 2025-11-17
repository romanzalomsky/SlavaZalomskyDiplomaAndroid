package com.zalomsky.sportscore.domain.usecase.league

import com.zalomsky.sportscore.data.LeagueRepositoryImpl
import com.zalomsky.sportscore.domain.models.LeagueModel
import javax.inject.Inject

class InsertLeagueUseCase @Inject constructor(
    private val leagueRepositoryImpl: LeagueRepositoryImpl
) {
    suspend operator fun invoke(league: LeagueModel) = leagueRepositoryImpl.insertLeague(league)
}