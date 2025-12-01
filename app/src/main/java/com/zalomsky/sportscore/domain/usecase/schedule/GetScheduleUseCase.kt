package com.zalomsky.sportscore.domain.usecase.schedule

import com.zalomsky.sportscore.data.MatchRepositoryImpl
import com.zalomsky.sportscore.domain.models.responses.MatchResponseModel
import javax.inject.Inject

class GetScheduleUseCase @Inject constructor(
    private val repository: MatchRepositoryImpl
) {
    suspend operator fun invoke(leagueId: String): Result<List<MatchResponseModel>> {
        if (leagueId.isBlank()) {
            return Result.failure(IllegalArgumentException("League ID cannot be blank."))
        }
        return repository.getSchedule(leagueId)
    }
}