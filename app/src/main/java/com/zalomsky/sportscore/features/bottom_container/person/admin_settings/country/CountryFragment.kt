package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.country

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.Country
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class CountryFragment : Fragment() {

    private val viewModel: CountryViewModel by viewModels()

    private lateinit var countryAdapter: CountryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.countriesRecyclerView)

        val countryNameEt = view.findViewById<TextInputEditText>(R.id.countryNameEditText)
        val flagEt = view.findViewById<TextInputEditText>(R.id.flagEditText)
        val addButton = view.findViewById<Button>(R.id.addCountryButton)

        countryAdapter = CountryAdapter(emptyList()) { countryToDelete ->
            showDeleteConfirmationDialog(countryToDelete)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = countryAdapter
        }

        viewModel.countries.observe(viewLifecycleOwner) { countriesList ->
            countriesList?.let {
                countryAdapter.updateList(it)
            }
        }

        if (viewModel.countries.value.isNullOrEmpty()) {
            viewModel.getCountriesList()
        }
        addButton.setOnClickListener {
            val name = countryNameEt.text.toString().trim()
            val flagUrl = flagEt.text.toString().trim()

            if (name.isNotEmpty() && flagUrl.isNotEmpty()) {

                val newCountry = Country(id = "", name = name, flag = flagUrl)

                viewModel.addCountry(newCountry) {
                    countryNameEt.setText("")
                    flagEt.setText("")
                    Toast.makeText(
                        context,
                        "Страна '${name}' успешно добавлена!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(context, "Заполните оба поля!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmationDialog(country: Country) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Подтверждение удаления")
            .setMessage("Вы уверены, что хотите удалить страну '${country.name}'?")
            .setPositiveButton("Удалить") { dialog, which ->
                viewModel.deleteCountry(country.id) {
                    Toast.makeText(context, "Страна '${country.name}' удалена!", Toast.LENGTH_SHORT).show()
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
        val view = inflater.inflate(R.layout.fragment_country, container, false)

        return view
    }

}