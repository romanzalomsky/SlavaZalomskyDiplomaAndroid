package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.player

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.PlayerModel
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import com.zalomsky.sportscore.domain.usecase.player.InsertPlayerUseCase
import com.zalomsky.sportscore.domain.usecase.player.PlayerUseCase
import com.zalomsky.sportscore.domain.usecase.team.TeamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerUseCase: PlayerUseCase,
    private val insertPlayerUseCase: InsertPlayerUseCase,
    private val teamUseCase: TeamUseCase
): ViewModel() {

    private val _players = MutableLiveData<List<PlayerResponseModel>>()
    val players: LiveData<List<PlayerResponseModel>>
        get() = _players

    private val _teams = MutableLiveData<List<TeamResponseModel>>()
    val teams: LiveData<List<TeamResponseModel>>
        get() = _teams

    fun getTeamsList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val teams = teamUseCase()
                _teams.postValue(teams)
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

    fun addPlayer(player: PlayerModel, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = insertPlayerUseCase(player)
                if (response.isSuccessful) {
                    getPlayersList()
                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }
            } catch (e: Exception) {
                Log.e("PlayerViewModel", "Error adding player: ${e.localizedMessage}")
            }
        }
    }
}
