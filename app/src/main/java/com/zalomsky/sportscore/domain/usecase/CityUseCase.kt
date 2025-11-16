package com.zalomsky.sportscore.domain.usecase

import com.zalomsky.sportscore.data.CityRepositoryImpl
import com.zalomsky.sportscore.domain.models.CityResponseModel
import javax.inject.Inject

class CityUseCase @Inject constructor(
    private val cityRepositoryImpl: CityRepositoryImpl
) {
    suspend operator fun invoke(): List<CityResponseModel> = cityRepositoryImpl.getCities()
}