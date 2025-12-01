package com.zalomsky.sportscore.features.bottom_container.favorite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import com.zalomsky.sportscore.domain.usecase.favorite.AddFavoriteUseCase
import com.zalomsky.sportscore.domain.usecase.favorite.GetFavoriteUseCase
import com.zalomsky.sportscore.domain.usecase.team.SearchTeamsSimpleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val searchTeamsSimpleUseCase: SearchTeamsSimpleUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val getFavoriteUseCase: GetFavoriteUseCase
): ViewModel() {

    private val _searchResults = MutableLiveData<List<TeamResponseModel>>()
    val searchResults: LiveData<List<TeamResponseModel>> = _searchResults

    private val _favoriteTeams = MutableLiveData<List<TeamResponseModel>>()
    val favoriteTeams: LiveData<List<TeamResponseModel>> = _favoriteTeams

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

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

    fun addTeamToFavorites(teamId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = addFavoriteUseCase(teamId)

                if (response.success) {
                    _message.postValue(response.message ?: "Команда успешно добавлена в избранное.")
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
}