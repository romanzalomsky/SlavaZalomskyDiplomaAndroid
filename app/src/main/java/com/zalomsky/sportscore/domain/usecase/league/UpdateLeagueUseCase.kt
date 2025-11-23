package com.zalomsky.sportscore.domain.usecase.league

import com.zalomsky.sportscore.data.LeagueRepositoryImpl
import com.zalomsky.sportscore.domain.models.LeagueModel
import com.zalomsky.sportscore.domain.models.responses.LeagueResponseModel
import retrofit2.Response
import javax.inject.Inject

class UpdateLeagueUseCase @Inject constructor(
    private val leagueRepositoryImpl: LeagueRepositoryImpl
) {
    suspend operator fun invoke(leagueId: String, league: LeagueModel): Response<LeagueResponseModel> {
        return leagueRepositoryImpl.updateLeague(leagueId, league)
    }
}