package com.zalomsky.sportscore.features.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.LoginRequest
import com.zalomsky.sportscore.domain.usecase.LoginUseCase
import com.zalomsky.sportscore.utils.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val preferenceManager: PreferenceManager
): ViewModel() {

    fun getLogin(loginRequest: LoginRequest, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                saveToken(loginUseCase(loginRequest = loginRequest).token)
                onSuccess()
            } catch (e: Exception) {
                Log.e("poiuyt", e.localizedMessage)
                onError()
            }
        }
    }

    fun saveToken(token: String) {
        preferenceManager.saveToken(token)
    }

    fun getToken(): String? {
        return preferenceManager.getToken()
    }
}