package com.zalomsky.sportscore.data

import com.zalomsky.sportscore.api.UserApi
import com.zalomsky.sportscore.domain.models.LoginRequest
import com.zalomsky.sportscore.domain.models.RegisterRequest
import com.zalomsky.sportscore.domain.models.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) {
    suspend fun getLogin(loginRequest: LoginRequest) = userApi.login(loginRequest = loginRequest)

    suspend fun getRegistration(request: RegisterRequest, asAdmin: Boolean) =
        if (asAdmin) userApi.adminRegistration(request = request) else userApi.registration(request = request)

    suspend fun getAllUsers() = userApi.getAllUsers()

    suspend fun deleteUser(userId: String) = userApi.deleteUser(userId)
}