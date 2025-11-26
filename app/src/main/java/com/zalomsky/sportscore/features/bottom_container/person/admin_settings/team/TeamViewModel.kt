package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.team

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.Country
import com.zalomsky.sportscore.domain.models.TeamModel
import com.zalomsky.sportscore.domain.models.responses.CityResponseModel
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import com.zalomsky.sportscore.domain.usecase.city.CityUseCase
import com.zalomsky.sportscore.domain.usecase.country.CountryUseCase
import com.zalomsky.sportscore.domain.usecase.team.InsertTeamUseCase
import com.zalomsky.sportscore.domain.usecase.team.TeamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val teamUseCase: TeamUseCase,
    private val insertTeamUseCase: InsertTeamUseCase,
    private val countryUseCase: CountryUseCase,
    private val cityUseCase: CityUseCase
): ViewModel() {

    private val _teams = MutableLiveData<List<TeamResponseModel>>()
    val teams: LiveData<List<TeamResponseModel>>
        get() = _teams

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
}