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
        val btnBack = view.findViewById<View>(R.id.btnBack)
        val countrySearchEt = view.findViewById<TextInputEditText>(R.id.countrySearchEditText)
        val fabAdd = view.findViewById<View>(R.id.fabAddCountry)
        val addFormContainer = view.findViewById<View>(R.id.addCountryFormContainer)
        val btnCancelAdd = view.findViewById<View>(R.id.btnCancelAdd)

        val countryNameEt = view.findViewById<TextInputEditText>(R.id.countryNameEditText)
        val flagEt = view.findViewById<TextInputEditText>(R.id.countryFlagEditText)
        val addButton = view.findViewById<Button>(R.id.addCountryButton)

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

        countrySearchEt.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                val filteredList = viewModel.countries.value?.filter { it.name.lowercase().contains(query) } ?: emptyList()
                countryAdapter.updateList(filteredList)
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

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
                    addFormContainer.visibility = View.GONE
                    fabAdd.visibility = View.VISIBLE
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