package com.zalomsky.sportscore.data

import com.zalomsky.sportscore.api.LeagueApi
import com.zalomsky.sportscore.domain.models.LeagueModel
import com.zalomsky.sportscore.domain.models.LeagueResponseModel
import javax.inject.Inject

class LeagueRepositoryImpl @Inject constructor(
    private val leagueApi: LeagueApi
) {

    suspend fun getLeagues(): List<LeagueResponseModel> = leagueApi.getLeagues()

    suspend fun insertLeague(league: LeagueModel) = leagueApi.insertLeague(league)
}