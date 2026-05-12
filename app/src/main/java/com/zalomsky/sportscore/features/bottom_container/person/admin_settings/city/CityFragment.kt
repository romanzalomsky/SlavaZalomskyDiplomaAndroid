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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.CityModel
import com.zalomsky.sportscore.domain.models.Country
import com.zalomsky.sportscore.domain.models.responses.CityResponseModel
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.country.CountryAdapter
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
        val btnBack = view.findViewById<View>(R.id.btnBack)
        val citySearchEt = view.findViewById<TextInputEditText>(R.id.citySearchEditText)
        val fabAdd = view.findViewById<View>(R.id.fabAddCity)
        val addFormContainer = view.findViewById<View>(R.id.addCityFormContainer)
        val btnCancelAdd = view.findViewById<View>(R.id.btnCancelAdd)

        val cityNameEt = view.findViewById<TextInputEditText>(R.id.cityNameEditText)
        val countrySelector = view.findViewById<AutoCompleteTextView>(R.id.countryAutoCompleteTextView)
        val addButton = view.findViewById<Button>(R.id.addCityButton)

        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        fabAdd.setOnClickListener {
            addFormContainer.visibility = View.VISIBLE
            fabAdd.visibility = View.GONE
        }

        btnCancelAdd.setOnClickListener {
            addFormContainer.visibility = View.GONE
            fabAdd.visibility = View.VISIBLE
        }

        cityAdapter = CityAdapter(emptyList()) { cityToDelete ->
            showDeleteConfirmationDialog(cityToDelete)
        }

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

        citySearchEt.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                val filteredList = viewModel.cities.value?.filter { it.name.lowercase().contains(query) } ?: emptyList()
                cityAdapter.updateList(filteredList)
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

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
                    addFormContainer.visibility = View.GONE
                    fabAdd.visibility = View.VISIBLE
                    Toast.makeText(context, "Город '${name}' успешно добавлен!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Заполните оба поля!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmationDialog(city: CityResponseModel) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Подтверждение удаления")
            .setMessage("Вы уверены, что хотите удалить город '${city.name}'?")
            .setPositiveButton("Удалить") { dialog, which ->
                viewModel.deleteCity(city.id) {
                    Toast.makeText(context, "Город '${city.name}' удален!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_city, container, false)
    }
}