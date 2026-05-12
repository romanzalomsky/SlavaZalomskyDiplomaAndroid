package com.zalomsky.sportscore.features.bottom_container.favorite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.responses.PlayerResponseModel
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import com.zalomsky.sportscore.domain.usecase.favorite.AddFavoriteUseCase
import com.zalomsky.sportscore.domain.usecase.favorite.DeleteFromFavoriteUseCase
import com.zalomsky.sportscore.domain.usecase.favorite.GetFavoriteUseCase
import com.zalomsky.sportscore.domain.usecase.player.AddFavoritePlayerUseCase
import com.zalomsky.sportscore.domain.usecase.player.DeleteFavoritePlayerUseCase
import com.zalomsky.sportscore.domain.usecase.player.GetFavoritePlayersUseCase
import com.zalomsky.sportscore.domain.usecase.player.SearchPlayersUseCase
import com.zalomsky.sportscore.domain.usecase.team.GetTeamByIdUseCase
import com.zalomsky.sportscore.domain.usecase.team.SearchTeamsSimpleUseCase
import com.zalomsky.sportscore.notions.Notifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val searchTeamsSimpleUseCase: SearchTeamsSimpleUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val getFavoriteUseCase: GetFavoriteUseCase,
    private val deleteFromFavoriteUseCase: DeleteFromFavoriteUseCase,
    private val getTeamByIdUseCase: GetTeamByIdUseCase,
    private val searchPlayersUseCase: SearchPlayersUseCase,
    private val addFavoritePlayerUseCase: AddFavoritePlayerUseCase,
    private val getFavoritePlayersUseCase: GetFavoritePlayersUseCase,
    private val deleteFavoritePlayerUseCase: DeleteFavoritePlayerUseCase,
    private val notifier: Notifier
): ViewModel() {

    private val _searchResults = MutableLiveData<List<TeamResponseModel>>()
    val searchResults: LiveData<List<TeamResponseModel>> = _searchResults

    private val _playerSearchResults = MutableLiveData<List<PlayerResponseModel>>()
    val playerSearchResults: LiveData<List<PlayerResponseModel>> = _playerSearchResults

    private val _favoriteTeams = MutableLiveData<List<TeamResponseModel>>()
    val favoriteTeams: LiveData<List<TeamResponseModel>> = _favoriteTeams

    private val _favoritePlayers = MutableLiveData<List<PlayerResponseModel>>()
    val favoritePlayers: LiveData<List<PlayerResponseModel>> = _favoritePlayers

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    fun clearMessage() {
        _message.postValue(null)
    }

    fun searchTeamsSimple(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val teams = searchTeamsSimpleUseCase(query)
                _searchResults.postValue(teams)
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Exception searching teams -> ${e.localizedMessage}")
                _searchResults.postValue(emptyList())
            }
        }
    }

    fun searchPlayers(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val players = searchPlayersUseCase(query)
                _playerSearchResults.postValue(players)
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Exception searching players -> ${e.localizedMessage}")
                _playerSearchResults.postValue(emptyList())
            }
        }
    }

    fun addTeamToFavorites(teamId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val team: TeamResponseModel? = getTeamByIdUseCase(teamId)

                val response = addFavoriteUseCase(teamId)

                if (response.success) {
                    _message.postValue(response.message ?: "Команда успешно добавлена в избранное.")

                    team?.let {
                        notifier.showTeamAddedNotification(it)
                    }

                    loadFavoriteTeams()
                } else {
                    _message.postValue(response.message ?: "Не удалось добавить команду в избранное.")
                }

            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Exception adding favorite -> ${e.localizedMessage}")
                _message.postValue("Ошибка сети: ${e.localizedMessage}")
            }
        }
    }

    fun addPlayerToFavorites(playerId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = addFavoritePlayerUseCase(playerId)
                if (response.success) {
                    _message.postValue(response.message ?: "Игрок успешно добавлен в избранное.")
                    loadFavoritePlayers()
                } else {
                    _message.postValue(response.message ?: "Не удалось добавить игрока в избранное.")
                }
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Exception adding favorite player -> ${e.localizedMessage}")
                _message.postValue("Ошибка сети: ${e.localizedMessage}")
            }
        }
    }

    fun loadFavoriteTeams() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val teams = getFavoriteUseCase()
                _favoriteTeams.postValue(teams)
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Exception loading favorites -> ${e.localizedMessage}")
                _favoriteTeams.postValue(emptyList())
                _message.postValue("Не удалось загрузить избранные команды.")
            }
        }
    }

    fun loadFavoritePlayers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val players = getFavoritePlayersUseCase()
                _favoritePlayers.postValue(players)
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Exception loading favorite players -> ${e.localizedMessage}")
                _favoritePlayers.postValue(emptyList())
                _message.postValue("Не удалось загрузить избранных игроков.")
            }
        }
    }

    fun deleteTeamFromFavorites(teamId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteFromFavoriteUseCase(teamId)
                loadFavoriteTeams()
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Exception deleting favorite -> ${e.localizedMessage}")
            }
        }
    }

    fun deletePlayerFromFavorites(playerId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteFavoritePlayerUseCase(playerId)
                loadFavoritePlayers()
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Exception deleting favorite player -> ${e.localizedMessage}")
            }
        }
    }
}