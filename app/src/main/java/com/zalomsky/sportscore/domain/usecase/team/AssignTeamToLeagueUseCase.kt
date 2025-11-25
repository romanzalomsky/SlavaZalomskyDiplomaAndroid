package com.zalomsky.sportscore.domain.usecase.team

import com.zalomsky.sportscore.data.LeagueRepositoryImpl
import retrofit2.Response
import javax.inject.Inject

class AssignTeamToLeagueUseCase @Inject constructor(
    private val repository: LeagueRepositoryImpl
) {
    suspend operator fun invoke(teamId: String, leagueId: String): Response<Unit> {
        return repository.assignTeamToLeague(teamId, leagueId)
    }
}