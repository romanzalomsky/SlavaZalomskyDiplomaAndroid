package com.zalomsky.sportscore.domain.usecase.favorite

import com.zalomsky.sportscore.data.TeamRepositoryImpl
import javax.inject.Inject

class DeleteFromFavoriteUseCase @Inject constructor(
    private val teamRepositoryImpl: TeamRepositoryImpl
) {

    suspend operator fun invoke(teamId: String) =
        teamRepositoryImpl.deleteFromFavorite(teamId)
}