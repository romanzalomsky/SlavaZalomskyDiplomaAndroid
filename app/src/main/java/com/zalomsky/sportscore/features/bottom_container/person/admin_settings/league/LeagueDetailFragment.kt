package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.league

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.Country
import com.zalomsky.sportscore.domain.models.LeagueModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeagueDetailFragment : Fragment() {

    private lateinit var leagueNameEditText: TextInputEditText
    private lateinit var leagueImageEditText: TextInputEditText
    private lateinit var countryAutoCompleteTextView: AutoCompleteTextView
    private lateinit var updateLeagueButton: Button

    private val viewModel: LeagueViewModel by viewModels()

    private val args: LeagueDetailFragmentArgs by navArgs()

    private var selectedCountryId: String? = null
    private var countryList: List<Country> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_league_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        viewModel.getCountriesList()

        val leagueId = args.leagueId
        viewModel.getLeagueDetails(leagueId)

        observeViewModel()
        setupUpdateListener(leagueId)
    }

    private fun initViews(view: View) {
        leagueNameEditText = view.findViewById(R.id.leagueNameEditTextDetails)
        leagueImageEditText = view.findViewById(R.id.leagueImageEditTextDetails)
        countryAutoCompleteTextView = view.findViewById(R.id.countryAutoCompleteTextViewLeagueDetails)
        updateLeagueButton = view.findViewById(R.id.updateLeagueButton)
    }

    private fun observeViewModel() {
        viewModel.countries.observe(viewLifecycleOwner) { countries ->
            countryList = countries
            setupCountryDropdown(countries)
        }

        viewModel.currentLeague.observe(viewLifecycleOwner) { league ->
            if (league != null) {
                leagueNameEditText.setText(league.leagueName)
                leagueImageEditText.setText(league.leagueImage)

                val currentCountry = countryList.find { it.id == league.countryId }
                if (currentCountry != null) {
                    countryAutoCompleteTextView.setText(currentCountry.name, false)
                    selectedCountryId = currentCountry.id
                }
            } else {
                Toast.makeText(context, "Лига не найдена или произошла ошибка загрузки.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun setupCountryDropdown(countries: List<Country>) {
        val countryNames = countries.map { it.name }
        val adapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, countryNames)

        countryAutoCompleteTextView.setAdapter(adapter)

        countryAutoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            selectedCountryId = countries.find { it.name == selectedName }?.id
        }
    }

    private fun setupUpdateListener(leagueId: String) {
        updateLeagueButton.setOnClickListener {
            val name = leagueNameEditText.text.toString().trim()
            val image = leagueImageEditText.text.toString().trim()
            val countryId = selectedCountryId

            if (name.isEmpty() || image.isEmpty() || countryId == null) {
                Toast.makeText(context, "Заполните все поля.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedLeague = LeagueModel(
                id = leagueId,
                leagueName = name,
                leagueImage = image,
                countryId = countryId
            )

            viewModel.updateLeague(leagueId, updatedLeague) {
                Toast.makeText(context, "Лига успешно обновлена!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }
}