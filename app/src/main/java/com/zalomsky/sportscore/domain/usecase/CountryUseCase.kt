package com.zalomsky.sportscore.domain.usecase

import com.zalomsky.sportscore.data.CountryRepositoryImpl
import com.zalomsky.sportscore.domain.models.Country
import javax.inject.Inject

class CountryUseCase @Inject constructor(
    private val countryRepositoryImpl: CountryRepositoryImpl
) {
    suspend operator fun invoke(): List<Country> = countryRepositoryImpl.getCountries()
}