package com.zalomsky.sportscore.domain.usecase.schedule

import com.zalomsky.sportscore.data.MatchRepositoryImpl
import com.zalomsky.sportscore.domain.models.responses.MatchResponseModel
import javax.inject.Inject

class GetFavoriteScheduleUseCase @Inject constructor(
    private val repository: MatchRepositoryImpl
) {
    suspend operator fun invoke(): Result<List<MatchResponseModel>> {
        return repository.getFavoriteSchedule()
    }
}