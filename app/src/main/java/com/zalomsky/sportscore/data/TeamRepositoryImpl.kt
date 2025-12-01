package com.zalomsky.sportscore.data

import com.zalomsky.sportscore.api.TeamApi
import com.zalomsky.sportscore.domain.models.FavoriteTeamRequest
import com.zalomsky.sportscore.domain.models.TeamModel
import com.zalomsky.sportscore.domain.models.responses.BaseResponse
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import javax.inject.Inject

class TeamRepositoryImpl @Inject constructor(
    private val teamApi: TeamApi
) {
    suspend fun getTeams(): List<TeamResponseModel> = teamApi.getTeams()

    suspend fun searchTeamsSimple(query: String): List<TeamResponseModel> =
        teamApi.searchTeamsSimple(query)

    suspend fun insertTeam(team: TeamModel) = teamApi.insertTeam(team)

    suspend fun addFavoriteTeam(teamId: String): BaseResponse {
        val request = FavoriteTeamRequest(teamId = teamId)
        return teamApi.addFavoriteTeam(request)
    }

    suspend fun getFavoriteTeams(): List<TeamResponseModel> {
        return teamApi.getFavoriteTeams()
    }
}