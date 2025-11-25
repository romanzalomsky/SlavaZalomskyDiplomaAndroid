package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.league

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.Country
import com.zalomsky.sportscore.domain.models.LeagueModel
import com.zalomsky.sportscore.domain.models.responses.LeagueResponseModel
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import com.zalomsky.sportscore.domain.usecase.country.CountryUseCase
import com.zalomsky.sportscore.domain.usecase.league.GetLeagueByIdUseCase
import com.zalomsky.sportscore.domain.usecase.league.InsertLeagueUseCase
import com.zalomsky.sportscore.domain.usecase.league.LeagueUseCase
import com.zalomsky.sportscore.domain.usecase.league.UpdateLeagueUseCase
import com.zalomsky.sportscore.domain.usecase.team.AssignTeamToLeagueUseCase
import com.zalomsky.sportscore.domain.usecase.team.GetTeamsByLeagueIdUseCase
import com.zalomsky.sportscore.domain.usecase.team.SearchTeamsUseCase
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

    private val _leagueTeams = MutableLiveData<List<TeamResponseModel>>()
    val leagueTeams: LiveData<List<TeamResponseModel>>
        get() = _leagueTeams

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
            try {
                val leagues = leagueUseCase()
                _leagues.postValue(leagues)
            } catch (e: Exception) {
                Log.e("LeagueViewModel", "Exception during request -> ${e.localizedMessage}")
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

                loadLeagueTeams(leagueId)
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

    fun assignTeamToLeague(teamId: String, leagueId: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = assignTeamToLeagueUseCase(teamId, leagueId)

                if (response.isSuccessful) {
                    onSuccess()
                    loadLeagueTeams(leagueId)
                } else {
                    Log.e("LeagueViewModel", "Team assignment failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("LeagueViewModel", "Exception assigning team to league -> ${e.localizedMessage}")
            }
        }
    }
}