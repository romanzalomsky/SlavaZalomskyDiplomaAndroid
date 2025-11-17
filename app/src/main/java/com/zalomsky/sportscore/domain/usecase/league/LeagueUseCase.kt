package com.zalomsky.sportscore.domain.usecase.league

import com.zalomsky.sportscore.data.LeagueRepositoryImpl
import com.zalomsky.sportscore.domain.models.LeagueResponseModel
import javax.inject.Inject

class LeagueUseCase @Inject constructor(
    private val leagueRepositoryImpl: LeagueRepositoryImpl
) {
    suspend operator fun invoke(): List<LeagueResponseModel> = leagueRepositoryImpl.getLeagues()
}