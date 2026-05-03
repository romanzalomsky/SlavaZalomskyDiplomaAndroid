package com.zalomsky.sportscore.features.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.LoginRequest
import com.zalomsky.sportscore.domain.models.RoleModel
import com.zalomsky.sportscore.domain.usecase.LoginUseCase
import com.zalomsky.sportscore.utils.JwtUtils
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
    private val preferenceManager: PreferenceManager
): ViewModel() {

    private val _userRole = MutableStateFlow<RoleModel?>(null)
    val userRole: StateFlow<RoleModel?> = _userRole

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getLogin(loginRequest: LoginRequest, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            _isLoading.value = true
            _error.value = null
            try {
                val response = loginUseCase(loginRequest = loginRequest)
                saveToken(response.token)
                fetchRole()
                onSuccess()
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login error: ${e.localizedMessage}")
                _error.value = e.localizedMessage
                onError()
            } finally {
                _isLoading.value = false
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
        val token = preferenceManager.getToken()
        if (token != null) {
            val role = JwtUtils.getRoleFromToken(token)
            role?.let {
                preferenceManager.saveRole(it.name)
                _userRole.value = it
            } ?: run {
                _error.value = "Не удалось определить роль пользователя из токена"
            }
        } else {
            _userRole.value = null
        }
    }

    fun logout() {
        preferenceManager.clearPreferences()
        _userRole.value = null
    }

    fun getSavedRole(): RoleModel? {
        return runCatching {
            preferenceManager.getRole()?.let { RoleModel.valueOf(it) }
        }.getOrNull()
    }
}
