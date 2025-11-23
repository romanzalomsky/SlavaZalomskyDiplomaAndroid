package com.zalomsky.sportscore.data

import com.zalomsky.sportscore.api.LeagueApi
import com.zalomsky.sportscore.domain.models.LeagueModel
import com.zalomsky.sportscore.domain.models.responses.LeagueResponseModel
import javax.inject.Inject

class LeagueRepositoryImpl @Inject constructor(
    private val leagueApi: LeagueApi
) {

    suspend fun getLeagues(): List<LeagueResponseModel> = leagueApi.getLeagues()

    suspend fun insertLeague(league: LeagueModel) = leagueApi.insertLeague(league)

    suspend fun getLeagueById(leagueId: String): LeagueResponseModel = leagueApi.getLeagueById(leagueId)

    suspend fun updateLeague(leagueId: String, league: LeagueModel) = leagueApi.updateLeague(leagueId, league)
}