package com.zalomsky.sportscore.api

import com.zalomsky.sportscore.domain.models.CityModel
import com.zalomsky.sportscore.domain.models.responses.CityResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CityApi {

    @GET("/auth/cities")
    suspend fun getCities(): List<CityResponseModel>

    @POST("/auth/cities/add")
    suspend fun insertCity(@Body city: CityModel): Response<CityResponseModel>
}