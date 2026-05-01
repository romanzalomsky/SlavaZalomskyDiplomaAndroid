package com.zalomsky.sportscore.api

import com.zalomsky.sportscore.domain.models.LoginRequest
import com.zalomsky.sportscore.domain.models.RegisterRequest
import com.zalomsky.sportscore.domain.models.responses.LoginResponse
import com.zalomsky.sportscore.domain.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {

    @POST("/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @GET("auth/refresh")
    suspend fun refreshToken(
        @Header("Authorization") token: String,
    ): Response<LoginResponse>

    @GET("/auth/get_user_info")
    suspend fun getUserInfo(): User? //todo: fix request

    @POST("/registration")
    suspend fun registration(@Body request: RegisterRequest): Response<User>

    @POST("/registration/admin")
    suspend fun adminRegistration(@Body request: RegisterRequest): Response<User>

    @GET("/auth/admin/users")
    suspend fun getAllUsers(): List<User>

    @DELETE("/auth/admin/users/{userId}")
    suspend fun deleteUser(@Path("userId") userId: String): Response<com.zalomsky.sportscore.domain.models.responses.BaseResponse>
}