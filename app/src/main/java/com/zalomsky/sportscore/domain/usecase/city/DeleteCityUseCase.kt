package com.zalomsky.sportscore.domain.usecase.city

import com.zalomsky.sportscore.data.CityRepositoryImpl
import javax.inject.Inject

class DeleteCityUseCase @Inject constructor(
    private val cityRepositoryImpl: CityRepositoryImpl
) {
    suspend operator fun invoke(cityId: String) = cityRepositoryImpl.deleteCity(cityId)
}