package com.zalomsky.sportscore.api

import com.zalomsky.sportscore.domain.models.Country
import com.zalomsky.sportscore.domain.models.responses.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CountryApi {

    @GET("auth/countries")
    suspend fun getCountries(): List<Country>

    @POST("auth/countries/add")
    suspend fun insertCountry(@Body country: Country): Response<Country>

    @DELETE("auth/countries/{countryId}")
    suspend fun deleteCountry(@Path("countryId") countryId: String): Response<BaseResponse>
}
