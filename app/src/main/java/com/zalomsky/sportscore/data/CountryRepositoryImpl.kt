package com.zalomsky.sportscore.data

import com.zalomsky.sportscore.api.CountryApi
import com.zalomsky.sportscore.domain.models.Country
import javax.inject.Inject

class CountryRepositoryImpl @Inject constructor(
    private val countryApi: CountryApi
) {

    suspend fun getCountries(): List<Country> = countryApi.getCountries()

    suspend fun insertCountry(country: Country) = countryApi.insertCountry(country)
}