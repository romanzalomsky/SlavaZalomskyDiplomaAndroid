package com.zalomsky.sportscore.domain.usecase.player

import com.zalomsky.sportscore.data.PlayerRepositoryImpl
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel
import javax.inject.Inject

class SearchPlayersForLeagueUseCase @Inject constructor(
    private val repository: PlayerRepositoryImpl
) {
    suspend operator fun invoke(query: String, leagueId: String): List<PlayerResponseModel> {
        return repository.searchPlayersForLeague(query, leagueId)
    }
}
