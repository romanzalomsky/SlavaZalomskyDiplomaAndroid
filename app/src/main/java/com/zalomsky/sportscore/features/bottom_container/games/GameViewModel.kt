package com.zalomsky.sportscore.features.bottom_container.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.SportType
import com.zalomsky.sportscore.domain.models.responses.LeaguesUiState
import com.zalomsky.sportscore.domain.models.responses.MatchResponseModel
import com.zalomsky.sportscore.domain.models.responses.ScheduleUiState
import com.zalomsky.sportscore.domain.usecase.league.LeagueUseCase
import com.zalomsky.sportscore.domain.usecase.schedule.GetFavoriteScheduleUseCase
import com.zalomsky.sportscore.domain.usecase.schedule.GetScheduleUseCase
import com.zalomsky.sportscore.notions.Notifier // Изменено на Notifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val getFavoriteScheduleUseCase: GetFavoriteScheduleUseCase,
    private val getScheduleUseCase: GetScheduleUseCase,
    private val getLeaguesUseCase: LeagueUseCase,
    private val repository: com.zalomsky.sportscore.data.MatchRepositoryImpl
) : ViewModel() {

    private val _leagueScheduleState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val leagueScheduleState: StateFlow<ScheduleUiState> = _leagueScheduleState

    private val _favoriteScheduleState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val favoriteScheduleState: StateFlow<ScheduleUiState> = _favoriteScheduleState

    private val _leaguesState = MutableStateFlow<LeaguesUiState>(LeaguesUiState.Loading)
    val leaguesState: StateFlow<LeaguesUiState> = _leaguesState

    private val _currentSport = MutableStateFlow(SportType.FOOTBALL)
    val currentSport: StateFlow<SportType> = _currentSport

    init {
        loadLeagues(SportType.FOOTBALL)
    }

    fun setSport(sportType: SportType) {
        _currentSport.value = sportType
        loadLeagues(sportType)
    }

    fun loadLeagues(sportType: SportType) {
        viewModelScope.launch {
            _leaguesState.value = LeaguesUiState.Loading
            getLeaguesUseCase(sportType.name)
                .onSuccess { leagues ->
                    _leaguesState.value = LeaguesUiState.Success(leagues)
                    if (leagues.isNotEmpty()) {
                        loadLeagueSchedule(leagues.first().id, sportType)
                    } else {
                        _leagueScheduleState.value = ScheduleUiState.Error("Лиги не найдены.")
                    }
                }
                .onFailure { error ->
                    _leaguesState.value = LeaguesUiState.Error(error.message ?: "Unknown error")
                    _leagueScheduleState.value = ScheduleUiState.Error("Ошибка загрузки расписания: ${error.message ?: "Unknown error"}")
                }
        }
    }

    fun loadLeagueSchedule(leagueId: String, sportType: SportType = _currentSport.value) {
        viewModelScope.launch {
            _leagueScheduleState.value = ScheduleUiState.Loading
            val result = if (sportType == SportType.TENNIS) {
                repository.getTennisSchedule(leagueId)
            } else {
                getScheduleUseCase(leagueId)
            }
            
            result.onSuccess { matches ->
                _leagueScheduleState.value = ScheduleUiState.Success(matches)
            }
            .onFailure { error ->
                _leagueScheduleState.value = ScheduleUiState.Error(error.message ?: "Unknown error")
            }
        }
    }

    fun loadFavoriteSchedule() {
        viewModelScope.launch {
            _favoriteScheduleState.value = ScheduleUiState.Loading
            getFavoriteScheduleUseCase()
                .onSuccess { matches ->
                    _favoriteScheduleState.value = ScheduleUiState.Success(matches)
                }
                .onFailure { error ->
                    _favoriteScheduleState.value = ScheduleUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    // Удален toggleMatchNotification и другие методы, связанные с уведомлениями о матчах
}