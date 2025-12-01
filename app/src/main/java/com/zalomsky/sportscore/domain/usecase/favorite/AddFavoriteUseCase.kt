package com.zalomsky.sportscore.domain.usecase.favorite

import com.zalomsky.sportscore.data.TeamRepositoryImpl
import javax.inject.Inject

class AddFavoriteUseCase @Inject constructor(
    private val teamRepositoryImpl: TeamRepositoryImpl
) {

    suspend operator fun invoke(teamId: String) = teamRepositoryImpl.addFavoriteTeam(teamId)
}