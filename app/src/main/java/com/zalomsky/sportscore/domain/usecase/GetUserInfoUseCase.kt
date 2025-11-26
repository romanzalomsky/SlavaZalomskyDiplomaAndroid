package com.zalomsky.sportscore.domain.usecase

import com.zalomsky.sportscore.data.UserRepositoryImpl
import com.zalomsky.sportscore.domain.models.User
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val userRepository: UserRepositoryImpl
) {
    suspend operator fun invoke(): User? = userRepository.getUser()
}