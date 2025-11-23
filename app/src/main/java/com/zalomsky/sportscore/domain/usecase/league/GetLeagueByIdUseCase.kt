package com.zalomsky.sportscore.domain.usecase.league

import com.zalomsky.sportscore.data.LeagueRepositoryImpl
import com.zalomsky.sportscore.domain.models.responses.LeagueResponseModel
import javax.inject.Inject

class GetLeagueByIdUseCase @Inject constructor(
    private val leagueRepositoryImpl: LeagueRepositoryImpl
) {

    suspend operator fun invoke(leagueId: String): LeagueResponseModel {
        return leagueRepositoryImpl.getLeagueById(leagueId)
    }
}