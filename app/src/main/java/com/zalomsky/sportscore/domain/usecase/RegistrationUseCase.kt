package com.zalomsky.sportscore.domain.usecase

import com.zalomsky.sportscore.data.UserRepositoryImpl
import com.zalomsky.sportscore.domain.models.RegisterRequest
import javax.inject.Inject

class RegistrationUseCase @Inject constructor(
    private val userRepositoryImpl: UserRepositoryImpl
) {

    suspend operator fun invoke(request: RegisterRequest, asAdmin: Boolean) =
        userRepositoryImpl.getRegistration(request = request, asAdmin = asAdmin)
}