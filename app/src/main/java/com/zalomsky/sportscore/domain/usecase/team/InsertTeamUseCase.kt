package com.zalomsky.sportscore.domain.usecase.team

import com.zalomsky.sportscore.data.TeamRepositoryImpl
import com.zalomsky.sportscore.domain.models.TeamModel
import javax.inject.Inject

class InsertTeamUseCase @Inject constructor(
    private val teamRepositoryImpl: TeamRepositoryImpl
) {

    suspend operator fun invoke(teamModel: TeamModel) = teamRepositoryImpl.insertTeam(teamModel)
}