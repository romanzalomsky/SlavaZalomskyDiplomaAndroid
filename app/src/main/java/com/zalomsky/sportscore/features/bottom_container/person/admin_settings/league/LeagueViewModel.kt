package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.league

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.Country
import com.zalomsky.sportscore.domain.models.LeagueModel
import com.zalomsky.sportscore.domain.models.LeagueResponseModel
import com.zalomsky.sportscore.domain.usecase.country.CountryUseCase
import com.zalomsky.sportscore.domain.usecase.league.InsertLeagueUseCase
import com.zalomsky.sportscore.domain.usecase.league.LeagueUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeagueViewModel @Inject constructor(
    private val leagueUseCase: LeagueUseCase,
    private val insertLeagueUseCase: InsertLeagueUseCase,
    private val countryUseCase: CountryUseCase
): ViewModel() {

    private val _leagues = MutableLiveData<List<LeagueResponseModel>>()
    val leagues: LiveData<List<LeagueResponseModel>>
        get() = _leagues

    private val _countries = MutableLiveData<List<Country>>()
    val countries: LiveData<List<Country>>
        get() = _countries

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
}