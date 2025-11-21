package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.city

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalomsky.sportscore.domain.models.CityModel
import com.zalomsky.sportscore.domain.models.responses.CityResponseModel
import com.zalomsky.sportscore.domain.models.Country
import com.zalomsky.sportscore.domain.usecase.city.CityUseCase
import com.zalomsky.sportscore.domain.usecase.country.CountryUseCase
import com.zalomsky.sportscore.domain.usecase.city.InsertCityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val cityUseCase: CityUseCase,
    private val insertCityUseCase: InsertCityUseCase,
    private val countryUseCase: CountryUseCase
): ViewModel() {

    private val _cities = MutableLiveData<List<CityResponseModel>>()
    val cities: LiveData<List<CityResponseModel>>
        get() = _cities

    private val _countries = MutableLiveData<List<Country>>()
    val countries: LiveData<List<Country>>
        get() = _countries

    fun getCountriesList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val countries = countryUseCase()
                _countries.postValue(countries)
            } catch (e: Exception) {
                Log.e("CityViewModel", "Exception getting countries -> ${e.localizedMessage}")
            }
        }
    }

    fun getCitiesList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cities = cityUseCase()
                _cities.postValue(cities)
            } catch (e: Exception) {
                Log.e("CityViewModel", "Exception during request -> ${e.localizedMessage}")
            }
        }
    }

    fun addCity(city: CityModel, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                insertCityUseCase(city)
                onSuccess()
                getCitiesList()
            } catch (e: Exception) {
                Log.e("CityViewModel", "Exception during city creation -> ${e.localizedMessage}")
            }
        }
    }
}