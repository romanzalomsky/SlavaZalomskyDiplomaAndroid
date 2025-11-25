package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.team

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
import com.zalomsky.sportscore.domain.models.LeagueModel
import com.zalomsky.sportscore.domain.models.TeamModel
import com.zalomsky.sportscore.domain.models.responses.CityResponseModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamFragment : Fragment() {

    private val viewModel: TeamViewModel by viewModels()
    private lateinit var teamAdapter: TeamAdapter

    private var selectedCountryId: String? = null
    private var countriesList: List<Country> = emptyList()

    private var selectedCityId: String? = null
    private var citiesList: List<CityResponseModel> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_team, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.teamsRecyclerView)

        val teamNameEt = view.findViewById<TextInputEditText>(R.id.teamNameEditText)
        val teamImageEt = view.findViewById<TextInputEditText>(R.id.teamImageEditText)
        val teamCreationDateEt = view.findViewById<TextInputEditText>(R.id.teamCreationDateEditText)
        val teamCoachEt = view.findViewById<TextInputEditText>(R.id.teamCoachEditText)
        val teamStadiumEt = view.findViewById<TextInputEditText>(R.id.teamStadiumEditText)

        val countrySelector = view.findViewById<AutoCompleteTextView>(R.id.countryAutoCompleteTextViewTeam)
        val citySelector = view.findViewById<AutoCompleteTextView>(R.id.cityAutoCompleteTextView)
        val addButton = view.findViewById<Button>(R.id.addTeamButton)

        teamAdapter = TeamAdapter(emptyList())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = teamAdapter
        }

        viewModel.teams.observe(viewLifecycleOwner) { teamsList ->
            teamsList?.let {
                teamAdapter.updateList(it)
            }
        }

        if (viewModel.teams.value.isNullOrEmpty()) {
            viewModel.getTeamsList()
        }

        viewModel.getCountriesList()
        viewModel.getCitiesList()

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

        viewModel.cities.observe(viewLifecycleOwner) { cities ->
            citiesList = cities
            if (cities.isNotEmpty()) {
                val cityNames = cities.map { it.name }
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    cityNames
                )
                citySelector.setAdapter(adapter)
            }
        }

        countrySelector.onItemClickListener = AdapterView.OnItemClickListener {
                parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            selectedCountryId = countriesList.find { it.name == selectedName }?.id
        }

        citySelector.onItemClickListener = AdapterView.OnItemClickListener {
                parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            selectedCityId = citiesList.find { it.name == selectedName }?.id
        }

        addButton.setOnClickListener {
            val name = teamNameEt.text.toString().trim()
            val image = teamImageEt.text.toString().trim()
            val creationDate = teamCreationDateEt.text.toString().trim()
            val teamCoach = teamCoachEt.text.toString().trim()
            val teamStadium = teamStadiumEt.text.toString().trim()
            val countryId = selectedCountryId
            val cityId = selectedCityId

            if (
                name.isNotEmpty() &&
                image.isNotEmpty() &&
                creationDate.isNotEmpty() &&
                teamCoach.isNotEmpty() &&
                teamStadium.isNotEmpty() &&
                !countryId.isNullOrEmpty() &&
                !cityId.isNullOrEmpty()) {

                val newTeam = TeamModel(
                    teamId = "",
                    teamName = name,
                    teamIcon = image,
                    dateOfFoundation = creationDate,
                    teamCoach = teamCoach,
                    teamStadium = teamStadium,
                    cityId = cityId,
                    countryId = countryId
                )

                viewModel.addTeam(newTeam) {
                    teamNameEt.setText("")
                    teamImageEt.setText("")
                    teamCreationDateEt.setText("")
                    teamCoachEt.setText("")
                    teamStadiumEt.setText("")
                    countrySelector.setText("")
                    countrySelector.setText("")
                    selectedCityId = null
                    selectedCountryId = null
                    Toast.makeText(context, "Команда '${name}' успешно добавлена!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}