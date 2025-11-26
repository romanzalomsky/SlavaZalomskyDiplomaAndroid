package com.zalomsky.sportscore.features.bottom_container.favorite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import com.zalomsky.sportscore.domain.usecase.team.SearchTeamsSimpleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val searchTeamsSimpleUseCase: SearchTeamsSimpleUseCase
): ViewModel() {

    private val _searchResults = MutableLiveData<List<TeamResponseModel>>()
    val searchResults: LiveData<List<TeamResponseModel>> = _searchResults

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
}