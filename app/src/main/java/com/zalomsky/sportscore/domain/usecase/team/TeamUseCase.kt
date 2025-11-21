package com.zalomsky.sportscore.domain.usecase.team

import com.zalomsky.sportscore.data.TeamRepositoryImpl
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import javax.inject.Inject

class TeamUseCase @Inject constructor(
    private val teamRepositoryImpl: TeamRepositoryImpl
) {
    suspend operator fun invoke(): List<TeamResponseModel> = teamRepositoryImpl.getTeams()
}