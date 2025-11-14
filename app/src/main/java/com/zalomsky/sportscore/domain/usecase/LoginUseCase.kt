package com.zalomsky.sportscore.domain.usecase

import com.zalomsky.sportscore.data.UserRepositoryImpl
import com.zalomsky.sportscore.domain.models.LoginRequest
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepositoryImpl: UserRepositoryImpl
) {

    suspend operator fun invoke(loginRequest: LoginRequest) =
        userRepositoryImpl.getLogin(loginRequest = loginRequest)
}