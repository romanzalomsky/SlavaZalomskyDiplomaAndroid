package com.zalomsky.sportscore.data

import com.zalomsky.sportscore.api.UserApi
import com.zalomsky.sportscore.domain.models.LoginRequest
import com.zalomsky.sportscore.domain.models.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) {
    suspend fun getLogin(loginRequest: LoginRequest) = userApi.login(loginRequest = loginRequest)

    suspend fun getUser() = userApi.getUserInfo()

    suspend fun getRegistration(user: User) = userApi.registration(user = user)
}