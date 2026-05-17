package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.league

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.Country
import com.zalomsky.sportscore.domain.models.LeagueModel
import com.zalomsky.sportscore.domain.models.responses.LeagueResponseModel
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import com.zalomsky.sportscore.domain.usecase.country.CountryUseCase
import com.zalomsky.sportscore.domain.usecase.league.GetLeagueByIdUseCase
import com.zalomsky.sportscore.domain.usecase.league.InsertLeagueUseCase
import com.zalomsky.sportscore.domain.usecase.league.LeagueUseCase
import com.zalomsky.sportscore.domain.usecase.league.UpdateLeagueUseCase
import com.zalomsky.sportscore.domain.usecase.player.AssignPlayerToLeagueUseCase
import com.zalomsky.sportscore.domain.usecase.player.GetPlayersByLeagueIdUseCase
import com.zalomsky.sportscore.domain.usecase.player.SearchPlayersForLeagueUseCase
import com.zalomsky.sportscore.domain.usecase.team.AssignTeamToLeagueUseCase
import com.zalomsky.sportscore.domain.usecase.team.GetTeamsByLeagueIdUseCase
import com.zalomsky.sportscore.domain.usecase.team.SearchTeamsUseCase
import com.zalomsky.sportscore.notions.Notifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeagueViewModel @Inject constructor(
    private val leagueUseCase: LeagueUseCase,
    private val insertLeagueUseCase: InsertLeagueUseCase,
    private val countryUseCase: CountryUseCase,
    private val getLeagueByIdUseCase: GetLeagueByIdUseCase,
    private val updateLeagueUseCase: UpdateLeagueUseCase,
    private val searchTeamsUseCase: SearchTeamsUseCase,
    private val assignTeamToLeagueUseCase: AssignTeamToLeagueUseCase,
    private val getTeamsByLeagueIdUseCase: GetTeamsByLeagueIdUseCase,
    private val getPlayersByLeagueIdUseCase: GetPlayersByLeagueIdUseCase,
    private val searchPlayersForLeagueUseCase: SearchPlayersForLeagueUseCase,
    private val assignPlayerToLeagueUseCase: AssignPlayerToLeagueUseCase,
    private val notifier: Notifier
): ViewModel() {

    private val _leagues = MutableLiveData<List<LeagueResponseModel>>()
    val leagues: LiveData<List<LeagueResponseModel>>
        get() = _leagues

    private val _countries = MutableLiveData<List<Country>>()
    val countries: LiveData<List<Country>>
        get() = _countries

    private val _currentLeague = MutableLiveData<LeagueResponseModel?>()
    val currentLeague: LiveData<LeagueResponseModel?>
        get() = _currentLeague

    private val _searchResults = MutableLiveData<List<TeamResponseModel>>()
    val searchResults: LiveData<List<TeamResponseModel>>
        get() = _searchResults

    private val _playerSearchResults = MutableLiveData<List<PlayerResponseModel>>()
    val playerSearchResults: LiveData<List<PlayerResponseModel>>
        get() = _playerSearchResults

    private val _leagueTeams = MutableLiveData<List<TeamResponseModel>>()
    val leagueTeams: LiveData<List<TeamResponseModel>>
        get() = _leagueTeams

    private val _leaguePlayers = MutableLiveData<List<PlayerResponseModel>>()
    val leaguePlayers: LiveData<List<PlayerResponseModel>>
        get() = _leaguePlayers

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    fun clearMessage() {
        _message.postValue(null)
    }

    fun loadLeagueParticipants(leagueId: String, isTennis: Boolean) {
        if (isTennis) {
            loadLeaguePlayers(leagueId)
        } else {
            loadLeagueTeams(leagueId)
        }
    }

    fun loadLeagueTeams(leagueId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val teams = getTeamsByLeagueIdUseCase(leagueId)
                _leagueTeams.postValue(teams)
            } catch (e: Exception) {
                Log.e("LeagueViewModel", "Exception loading league teams -> ${e.localizedMessage}")
                _leagueTeams.postValue(emptyList())
            }
        }
    }

    fun loadLeaguePlayers(leagueId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val players = getPlayersByLeagueIdUseCase(leagueId)
                _leaguePlayers.postValue(players)
            } catch (e: Exception) {
                Log.e("LeagueViewModel", "Exception loading league players -> ${e.localizedMessage}")
                _leaguePlayers.postValue(emptyList())
            }
        }
    }

    fun getCountriesList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val countries = countryUseCase()
                _countries.postValue(countries)
            } catch (e: Exception) {
                Log.e("LeagueViewModel", "Exception getting countries -> ${e.localizedMessage}")
            }
        }
    }

    fun getLeaguesList() {
        viewModelScope.launch(Dispatchers.IO) {
            leagueUseCase()
                .onSuccess { leagues ->
                    _leagues.postValue(leagues)
                }
                .onFailure { error ->
                    Log.e("LeagueViewModel", "Exception during request -> ${error.localizedMessage}")
                    _leagues.postValue(emptyList())
                }
        }
    }

    fun addLeague(league: LeagueModel, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                insertLeagueUseCase(league)
                onSuccess()
                getLeaguesList()
            } catch (e: Exception) {
                Log.e("LeagueViewModel", "Exception during league creation -> ${e.localizedMessage}")
            }
        }
    }

    fun getLeagueDetails(leagueId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val league = getLeagueByIdUseCase(leagueId)
                _currentLeague.postValue(league)

                val isTennis = league.sportType.equals("TENNIS", true)
                loadLeagueParticipants(leagueId, isTennis)
            } catch (e: Exception) {
                Log.e("LeagueViewModel", "Exception getting league details -> ${e.localizedMessage}")
                _currentLeague.postValue(null)
            }
        }
    }

    fun updateLeague(leagueId: String, league: LeagueModel, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = updateLeagueUseCase(leagueId, league)

                if (response.isSuccessful) {
                    onSuccess()
                    getLeaguesList()
                } else {
                    Log.e("LeagueViewModel", "Update failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("LeagueViewModel", "Exception during league update -> ${e.localizedMessage}")
            }
        }
    }

    fun searchTeams(query: String, leagueId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val teams = searchTeamsUseCase(query, leagueId)
                _searchResults.postValue(teams)
            } catch (e: Exception) {
                Log.e("LeagueViewModel", "Exception searching teams -> ${e.localizedMessage}")
                _searchResults.postValue(emptyList())
            }
        }
    }

    fun searchPlayers(query: String, leagueId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val players = searchPlayersForLeagueUseCase(query, leagueId)
                _playerSearchResults.postValue(players)
            } catch (e: Exception) {
                Log.e("LeagueViewModel", "Exception searching players -> ${e.localizedMessage}")
                _playerSearchResults.postValue(emptyList())
            }
        }
    }

    fun assignTeamToLeague(teamId: String, leagueId: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val teamToNotify = _searchResults.value?.find { it.teamId == teamId }

                val response = assignTeamToLeagueUseCase(teamId, leagueId)

                if (response.isSuccessful) {
                    onSuccess()
                    loadLeagueTeams(leagueId)

                    teamToNotify?.let {
                        notifier.showTeamAddedNotification(it)
                        Log.d("LeagueViewModel", "Уведомление о добавлении команды ${it.teamName} отправлено.")
                    }

                } else {
                    _message.postValue("Ошибка: ${response.message() ?: response.code()}")
                    Log.e("LeagueViewModel", "Team assignment failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("LeagueViewModel", "Exception assigning team to league -> ${e.localizedMessage}")
            }
        }
    }

    fun assignPlayerToLeague(playerId: String, leagueId: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = assignPlayerToLeagueUseCase(playerId, leagueId)

                if (response.isSuccessful) {
                    onSuccess()
                    loadLeaguePlayers(leagueId)
                } else {
                    _message.postValue("Ошибка: ${response.message() ?: response.code()}")
                    Log.e("LeagueViewModel", "Player assignment failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("LeagueViewModel", "Exception assigning player to league -> ${e.localizedMessage}")
            }
        }
    }
}