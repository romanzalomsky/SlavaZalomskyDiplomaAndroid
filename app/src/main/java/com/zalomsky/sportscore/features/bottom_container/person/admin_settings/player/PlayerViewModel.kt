package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.player

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.Country
import com.zalomsky.sportscore.domain.models.PlayerModel
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel
import com.zalomsky.sportscore.domain.usecase.country.CountryUseCase
import com.zalomsky.sportscore.domain.usecase.player.InsertPlayerUseCase
import com.zalomsky.sportscore.domain.usecase.player.PlayerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerUseCase: PlayerUseCase,
    private val insertPlayerUseCase: InsertPlayerUseCase,
    private val countryUseCase: CountryUseCase
): ViewModel() {

    private val _players = MutableLiveData<List<PlayerResponseModel>>()
    val players: LiveData<List<PlayerResponseModel>>
        get() = _players

    private val _countries = MutableLiveData<List<Country>>()
    val countries: LiveData<List<Country>>
        get() = _countries

    fun getCountriesList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val countries = countryUseCase()
                _countries.postValue(countries)
            } catch (e: Exception) {
                Log.e("PlayerViewModel", "Exception getting countries -> ${e.localizedMessage}")
            }
        }
    }

    fun getPlayersList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val players = playerUseCase()
                _players.postValue(players)
            } catch (e: Exception) {
                Log.e("PlayerViewModel", "Exception during request -> ${e.localizedMessage}")
            }
        }
    }

    fun addPlayer(player: PlayerModel, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                insertPlayerUseCase(player)
                onSuccess()
                getPlayersList()
            } catch (e: Exception) {
                Log.e("PlayerViewModel", "Exception during league creation -> ${e.localizedMessage}")
            }
        }
    }
}