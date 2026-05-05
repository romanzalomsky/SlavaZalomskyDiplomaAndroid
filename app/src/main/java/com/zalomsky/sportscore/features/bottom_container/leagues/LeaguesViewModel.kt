package com.zalomsky.sportscore.features.bottom_container.leagues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.responses.LeagueResponseModel
import com.zalomsky.sportscore.domain.models.responses.LeaguesUiState
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import com.zalomsky.sportscore.domain.usecase.league.LeagueUseCase
import com.zalomsky.sportscore.domain.usecase.team.GetTeamByIdUseCase
import com.zalomsky.sportscore.api.PlayerApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaguesViewModel @Inject constructor(
    private val getLeaguesUseCase: LeagueUseCase,
    private val playerApi: PlayerApi,
    private val getTeamByIdUseCase: GetTeamByIdUseCase
) : ViewModel() {

    private val _leaguesState = MutableStateFlow<LeaguesUiState>(LeaguesUiState.Loading)
    val leaguesState: StateFlow<LeaguesUiState> = _leaguesState

    private val _selectedLeagueId = MutableStateFlow<String?>(null)
    val selectedLeague: StateFlow<LeagueResponseModel?> = combine(leaguesState, _selectedLeagueId) { state, id ->
        if (state is LeaguesUiState.Success && id != null) {
            state.leagues.find { it.id == id }
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _selectedTeam = MutableStateFlow<TeamResponseModel?>(null)
    val selectedTeam: StateFlow<TeamResponseModel?> = _selectedTeam

    private val _isTeamLoading = MutableStateFlow(false)
    val isTeamLoading: StateFlow<Boolean> = _isTeamLoading

    private val _teamPlayers = MutableStateFlow<List<PlayerResponseModel>>(emptyList())
    val teamPlayers: StateFlow<List<PlayerResponseModel>> = _teamPlayers

    init {
        loadLeagues()
    }

    fun loadLeagues() {
        viewModelScope.launch {
            _leaguesState.value = LeaguesUiState.Loading
            getLeaguesUseCase()
                .onSuccess { leagues ->
                    _leaguesState.value = LeaguesUiState.Success(leagues)
                }
                .onFailure { error ->
                    _leaguesState.value = LeaguesUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun selectLeague(leagueId: String) {
        _selectedLeagueId.value = leagueId
    }

    fun loadTeamDetails(teamId: String) {
        viewModelScope.launch {
            _isTeamLoading.value = true
            try {
                val team = getTeamByIdUseCase(teamId)
                _selectedTeam.value = team
                
                // Currently playerApi returns all players, we might need filtering in the future
                val allPlayers = playerApi.getPlayers()
                _teamPlayers.value = allPlayers
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isTeamLoading.value = false
            }
        }
    }
}