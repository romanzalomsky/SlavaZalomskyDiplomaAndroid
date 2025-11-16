package com.zalomsky.sportscore.domain.usecase.city

import com.zalomsky.sportscore.data.CityRepositoryImpl
import com.zalomsky.sportscore.domain.models.CityModel
import javax.inject.Inject

class InsertCityUseCase @Inject constructor(
    private val cityRepositoryImpl: CityRepositoryImpl
) {
    suspend operator fun invoke(city: CityModel) = cityRepositoryImpl.insertCity(city)
}