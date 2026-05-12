package com.zalomsky.sportscore.data

import com.zalomsky.sportscore.api.MatchApi
import com.zalomsky.sportscore.domain.models.responses.MatchResponseModel
import javax.inject.Inject

class MatchRepositoryImpl @Inject constructor(
    private val matchApi: MatchApi
) {

    suspend fun getSchedule(leagueId: String): Result<List<MatchResponseModel>> {
        return try {
            val result = matchApi.getScheduleByLeagueId(leagueId)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTennisSchedule(leagueId: String): Result<List<MatchResponseModel>> {
        return try {
            val result = matchApi.getTennisScheduleByLeagueId(leagueId)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFavoriteSchedule(): Result<List<MatchResponseModel>> {
        return try {
            val result = matchApi.getFavoriteSchedule()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}