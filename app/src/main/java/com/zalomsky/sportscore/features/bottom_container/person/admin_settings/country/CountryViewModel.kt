package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.country

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.Country
import com.zalomsky.sportscore.domain.usecase.CountryUseCase
import com.zalomsky.sportscore.domain.usecase.InsertCountryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryViewModel @Inject constructor(
    private val countryUseCase: CountryUseCase,
    private val insertCountryUseCase: InsertCountryUseCase
): ViewModel() {

    private val _country = MutableStateFlow<Country?>(null)
    val country: StateFlow<Country?>
        get() = _country

    private val _countries = MutableLiveData<List<Country>>()
    val countries: LiveData<List<Country>>
        get() = _countries

    fun getCountriesList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val countries = countryUseCase()
                _countries.postValue(countries)
            } catch (e: Exception) {
                Log.e("asdfghjk", "Exception during request -> ${e.localizedMessage}")
            }
        }
    }

    fun addCountry(country: Country, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                insertCountryUseCase(country)
                onSuccess()
            } catch (e: Exception) {
                Log.e("asdfghjk", "Exception during request -> ${e.localizedMessage}")
            }
        }
    }
}