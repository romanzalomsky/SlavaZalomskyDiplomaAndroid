package com.zalomsky.sportscore.domain.usecase.team

import com.zalomsky.sportscore.data.TeamRepositoryImpl
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import javax.inject.Inject

class SearchTeamsSimpleUseCase @Inject constructor(
    private val repository: TeamRepositoryImpl
) {
    suspend operator fun invoke(query: String): List<TeamResponseModel> {
        return repository.searchTeamsSimple(query)
    }
}