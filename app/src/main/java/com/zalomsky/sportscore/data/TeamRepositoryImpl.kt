package com.zalomsky.sportscore.data

import com.zalomsky.sportscore.api.TeamApi
import com.zalomsky.sportscore.domain.models.TeamModel
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import javax.inject.Inject

class TeamRepositoryImpl @Inject constructor(
    private val teamApi: TeamApi
) {
    suspend fun getTeams(): List<TeamResponseModel> = teamApi.getTeams()

    suspend fun searchTeamsSimple(query: String): List<TeamResponseModel> =
        teamApi.searchTeamsSimple(query)

    suspend fun insertTeam(team: TeamModel) = teamApi.insertTeam(team)
}