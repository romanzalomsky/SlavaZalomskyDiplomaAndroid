package com.zalomsky.sportscore.features.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.LoginRequest
import com.zalomsky.sportscore.domain.models.RoleModel
import com.zalomsky.sportscore.domain.usecase.GetUserInfoUseCase
import com.zalomsky.sportscore.domain.usecase.LoginUseCase
import com.zalomsky.sportscore.utils.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val preferenceManager: PreferenceManager
): ViewModel() {

    private val _userRole = MutableStateFlow<RoleModel?>(null)
    val userRole: StateFlow<RoleModel?> = _userRole

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

    fun fetchRole() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = getUserInfoUseCase()
                val role = user?.roleModel

                role?.let {
                    preferenceManager.saveRole(it.name)
                }

                _userRole.value = role
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error fetching user info: ${e.localizedMessage}")
                _userRole.value = null
            }
        }
    }
}