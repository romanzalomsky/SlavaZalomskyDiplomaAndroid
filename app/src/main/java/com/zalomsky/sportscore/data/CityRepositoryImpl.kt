package com.zalomsky.sportscore.data

import com.zalomsky.sportscore.api.CityApi
import com.zalomsky.sportscore.domain.models.CityModel
import com.zalomsky.sportscore.domain.models.CityResponseModel
import javax.inject.Inject

class CityRepositoryImpl @Inject constructor(
    private val cityApi: CityApi
) {
    suspend fun getCities(): List<CityResponseModel> = cityApi.getCities()

    suspend fun insertCity(city: CityModel) = cityApi.insertCity(city)
}