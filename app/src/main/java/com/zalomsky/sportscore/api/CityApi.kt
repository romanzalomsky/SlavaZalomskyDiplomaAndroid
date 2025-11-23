package com.zalomsky.sportscore.api

import com.zalomsky.sportscore.domain.models.CityModel
import com.zalomsky.sportscore.domain.models.responses.BaseResponse
import com.zalomsky.sportscore.domain.models.responses.CityResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CityApi {

    @GET("/auth/cities")
    suspend fun getCities(): List<CityResponseModel>

    @POST("/auth/cities/add")
    suspend fun insertCity(@Body city: CityModel): Response<CityResponseModel>

    @DELETE("/auth/cities/{cityId}")
    suspend fun deleteCity(@Path("cityId") cityId: String): Response<BaseResponse>
}