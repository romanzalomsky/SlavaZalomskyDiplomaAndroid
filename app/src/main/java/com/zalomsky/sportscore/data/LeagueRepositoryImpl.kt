package com.zalomsky.sportscore.data

import com.zalomsky.sportscore.api.LeagueApi
import com.zalomsky.sportscore.api.TeamApi
import com.zalomsky.sportscore.domain.models.LeagueIdWrapper
import com.zalomsky.sportscore.domain.models.LeagueModel
import com.zalomsky.sportscore.domain.models.responses.LeagueResponseModel
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import retrofit2.Response
import javax.inject.Inject

class LeagueRepositoryImpl @Inject constructor(
    private val leagueApi: LeagueApi,
    private val teamApi: TeamApi
) {

    suspend fun getLeagues(sportType: String? = null): List<LeagueResponseModel> = leagueApi.getLeagues(sportType)

    suspend fun insertLeague(league: LeagueModel) = leagueApi.insertLeague(league)

    suspend fun getLeagueById(leagueId: String): LeagueResponseModel = leagueApi.getLeagueById(leagueId)

    suspend fun updateLeague(leagueId: String, league: LeagueModel) = leagueApi.updateLeague(leagueId, league)

    suspend fun searchTeams(query: String, leagueId: String): List<TeamResponseModel> {
        return teamApi.searchTeams(query, leagueId)
    }

    suspend fun assignTeamToLeague(teamId: String, leagueId: String): Response<Unit> {
        val wrapper = LeagueIdWrapper(leagueId = leagueId)
        return teamApi.assignTeamToLeague(teamId, wrapper)
    }

    suspend fun getTeamsByLeagueId(leagueId: String): List<TeamResponseModel> {
        return leagueApi.getTeamsByLeagueId(leagueId)
    }

    suspend fun getPlayersByLeagueId(leagueId: String): List<PlayerResponseModel> {
        return leagueApi.getPlayersByLeagueId(leagueId)
    }
}