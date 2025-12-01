package com.zalomsky.sportscore.domain.usecase.favorite

import com.zalomsky.sportscore.data.TeamRepositoryImpl
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import javax.inject.Inject

class GetFavoriteUseCase @Inject constructor(
    private val teamRepositoryImpl: TeamRepositoryImpl
) {

    suspend operator fun invoke(): List<TeamResponseModel> = teamRepositoryImpl.getFavoriteTeams()
}