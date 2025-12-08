package com.zalomsky.sportscore.domain.usecase.team

import com.zalomsky.sportscore.data.TeamRepositoryImpl
import javax.inject.Inject

class GetTeamByIdUseCase @Inject constructor(
    private val teamRepositoryImpl: TeamRepositoryImpl
) {

    suspend operator fun invoke(teamId: String) = teamRepositoryImpl.getTeamById(teamId)
}