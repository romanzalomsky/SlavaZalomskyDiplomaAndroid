package com.zalomsky.sportscore.features.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.RegisterRequest
import com.zalomsky.sportscore.domain.usecase.RegistrationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registrationUseCase: RegistrationUseCase
) : ViewModel() {
    private val _user = MutableLiveData<RegisterRequest>()
    val user: LiveData<RegisterRequest>
        get() = _user

    fun createNewUser(request: RegisterRequest, asAdmin: Boolean, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                registrationUseCase(request = request, asAdmin = asAdmin)
                onSuccess()
            } catch (e: Exception) {
                Log.e("asdfghjk", "Exception during request -> ${e.localizedMessage}")
                onError()
            }
        }
    }
}