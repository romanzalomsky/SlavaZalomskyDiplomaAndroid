package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.team

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.Country
import com.zalomsky.sportscore.domain.models.TeamModel
import com.zalomsky.sportscore.domain.models.responses.CityResponseModel
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import com.zalomsky.sportscore.domain.usecase.city.CityUseCase
import com.zalomsky.sportscore.domain.usecase.country.CountryUseCase
import com.zalomsky.sportscore.domain.usecase.player.PlayerUseCase
import com.zalomsky.sportscore.domain.usecase.team.GetTeamByIdUseCase
import com.zalomsky.sportscore.domain.usecase.team.InsertTeamUseCase
import com.zalomsky.sportscore.domain.usecase.team.TeamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val teamUseCase: TeamUseCase,
    private val getTeamByIdUseCase: GetTeamByIdUseCase,
    private val insertTeamUseCase: InsertTeamUseCase,
    private val countryUseCase: CountryUseCase,
    private val cityUseCase: CityUseCase,
    private val playerUseCase: PlayerUseCase
): ViewModel() {

    private val _teams = MutableLiveData<List<TeamResponseModel>>()
    val teams: LiveData<List<TeamResponseModel>>
        get() = _teams

    private val _team = MutableLiveData<TeamResponseModel?>()
    val team: LiveData<TeamResponseModel?>
        get() = _team

    private val _players = MutableLiveData<List<PlayerResponseModel>>()
    val players: LiveData<List<PlayerResponseModel>>
        get() = _players

    private val _countries = MutableLiveData<List<Country>>()
    val countries: LiveData<List<Country>>
        get() = _countries

    private val _cities = MutableLiveData<List<CityResponseModel>>()
    val cities: LiveData<List<CityResponseModel>>
        get() = _cities

    fun getCountriesList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val countries = countryUseCase()
                _countries.postValue(countries)
            } catch (e: Exception) {
                Log.e("TeamViewModel", "Exception getting countries -> ${e.localizedMessage}")
            }
        }
    }

    fun getTeamById(teamId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val team = getTeamByIdUseCase.invoke(teamId)
                _team.postValue(team)
            } catch (e: Exception) {
                Log.e("TeamViewModel", "Exception getting team by ID -> ${e.localizedMessage}")
                _team.postValue(null)
            }
        }
    }

    fun getCitiesList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cities = cityUseCase()
                _cities.postValue(cities)
            } catch (e: Exception) {
                Log.e("TeamViewModel", "Exception getting countries -> ${e.localizedMessage}")
            }
        }
    }

    fun getTeamsList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val teams = teamUseCase()
                _teams.postValue(teams)
            } catch (e: Exception) {
                Log.e("TeamViewModel", "Exception during request -> ${e.localizedMessage}")
            }
        }
    }

    fun addTeam(team: TeamModel, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                insertTeamUseCase(team)
                onSuccess()
                getTeamsList()
            } catch (e: Exception) {
                Log.e("TeamViewModel", "Exception during league creation -> ${e.localizedMessage}")
            }
        }
    }

    fun getPlayersByTeamId(teamId: String) {
        viewModelScope.launch {
            try {
                val allPlayers = playerUseCase()
                val filteredPlayers = allPlayers.filter { player ->
                    player.teamId == teamId
                }
                _players.value = filteredPlayers
            } catch (e: Exception) {
                Log.e("PlayerViewModel", "Exception during request -> ${e.localizedMessage}")
            }
        }
    }
}