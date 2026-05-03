package com.zalomsky.sportscore.domain.usecase

import android.util.Log
import com.zalomsky.sportscore.data.UserRepositoryImpl
import com.zalomsky.sportscore.domain.models.User
import retrofit2.Response
import javax.inject.Inject

//class GetUserInfoUseCase @Inject constructor(
//    private val userRepository: UserRepositoryImpl
//) {
//    suspend operator fun invoke(): User? {
//        val response: Response<User> = userRepository.getUser()
//        return if (response.isSuccessful) {
//            response.body()
//        } else {
//            val errorBody = response.errorBody()?.string()
//            Log.e("GetUserInfoUseCase", "Error fetching user info: ${response.code()} - $errorBody")
//            null
//        }
//    }
//}
