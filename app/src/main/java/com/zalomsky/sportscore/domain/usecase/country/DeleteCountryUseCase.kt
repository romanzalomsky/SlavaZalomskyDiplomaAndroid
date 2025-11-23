package com.zalomsky.sportscore.domain.usecase.country

import com.zalomsky.sportscore.data.CountryRepositoryImpl
import javax.inject.Inject

class DeleteCountryUseCase @Inject constructor(
    private val countryRepositoryImpl: CountryRepositoryImpl
) {
    suspend operator fun invoke(countryId: String) = countryRepositoryImpl.deleteCountry(countryId)
}