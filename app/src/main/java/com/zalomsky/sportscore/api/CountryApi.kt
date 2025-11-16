package com.zalomsky.sportscore.api

import com.zalomsky.sportscore.domain.models.Country
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CountryApi {

    @GET("/auth/countries")
    suspend fun getCountries(): List<Country>

    @POST("/auth/countries/add")
    suspend fun insertCountry(@Body country: Country): Response<Country>

}