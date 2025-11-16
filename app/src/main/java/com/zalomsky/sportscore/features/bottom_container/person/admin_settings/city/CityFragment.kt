package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.city

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.CityModel
import com.zalomsky.sportscore.domain.models.Country
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CityFragment : Fragment() {

    private val viewModel: CityViewModel by viewModels()

    private lateinit var cityAdapter: CityAdapter

    private var selectedCountryId: String? = null
    private var countriesList: List<Country> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.citiesRecyclerView)

        val cityNameEt = view.findViewById<TextInputEditText>(R.id.cityNameEditText)
        val countrySelector = view.findViewById<AutoCompleteTextView>(R.id.countryAutoCompleteTextView)
        val addButton = view.findViewById<Button>(R.id.addCityButton)

        cityAdapter = CityAdapter(emptyList())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cityAdapter
        }

        viewModel.getCountriesList()

        viewModel.cities.observe(viewLifecycleOwner) { citiesList ->
            citiesList?.let {
                cityAdapter.updateList(it)
            }
        }

        if (viewModel.cities.value.isNullOrEmpty()) {
            viewModel.getCitiesList()
        }

        viewModel.countries.observe(viewLifecycleOwner) { countries ->
            countriesList = countries
            if (countries.isNotEmpty()) {
                val countryNames = countries.map { it.name }
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    countryNames
                )
                countrySelector.setAdapter(adapter)
            }
        }

        countrySelector.onItemClickListener = AdapterView.OnItemClickListener {
                parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            selectedCountryId = countriesList.find { it.name == selectedName }?.id
        }

        addButton.setOnClickListener {
            val name = cityNameEt.text.toString().trim()
            val countryId = selectedCountryId

            if (name.isNotEmpty() && !countryId.isNullOrEmpty()) {

                val newCity = CityModel(id = "", name = name, countryId = countryId)

                viewModel.addCity(newCity) {
                    cityNameEt.setText("")
                    countrySelector.setText("")
                    selectedCountryId = null
                    Toast.makeText(context, "Город '${name}' успешно добавлен!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Заполните оба поля!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_city, container, false)
    }
}