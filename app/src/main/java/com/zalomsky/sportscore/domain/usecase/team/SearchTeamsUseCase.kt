package com.zalomsky.sportscore.domain.usecase.team

import com.zalomsky.sportscore.data.LeagueRepositoryImpl
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import javax.inject.Inject

class SearchTeamsUseCase @Inject constructor(
    private val repository: LeagueRepositoryImpl
) {
    suspend operator fun invoke(query: String, leagueId: String): List<TeamResponseModel> {
        return repository.searchTeams(query, leagueId)
    }
}