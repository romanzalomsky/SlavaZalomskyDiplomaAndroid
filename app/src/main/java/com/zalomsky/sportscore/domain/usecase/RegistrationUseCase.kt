package com.zalomsky.sportscore.domain.usecase

import com.zalomsky.sportscore.data.UserRepositoryImpl
import com.zalomsky.sportscore.domain.models.User
import javax.inject.Inject

class RegistrationUseCase @Inject constructor(
    private val userRepositoryImpl: UserRepositoryImpl
) {

    suspend operator fun invoke(user: User) =
        userRepositoryImpl.getRegistration(user = user)
}