package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.league

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
import com.zalomsky.sportscore.domain.models.Country
import com.zalomsky.sportscore.domain.models.LeagueModel
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.city.CityAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeagueFragment : Fragment() {

    private val viewModel: LeagueViewModel by viewModels()
    private lateinit var leagueAdapter: LeagueAdapter

    private var selectedCountryId: String? = null
    private var countriesList: List<Country> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_league, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.leaguesRecyclerView)

        val leagueNameEt = view.findViewById<TextInputEditText>(R.id.leagueNameEditText)
        val leagueImageEt = view.findViewById<TextInputEditText>(R.id.leagueImageEditText)
        val leagueTeamsEt = view.findViewById<TextInputEditText>(R.id.leagueTeamsEditText)

        val countrySelector = view.findViewById<AutoCompleteTextView>(R.id.countryAutoCompleteTextViewLeague)
        val addButton = view.findViewById<Button>(R.id.addLeagueButton)

        leagueAdapter = LeagueAdapter(emptyList())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = leagueAdapter
        }

        viewModel.getCountriesList()

        viewModel.leagues.observe(viewLifecycleOwner) { leaguesList ->
            leaguesList?.let {
                leagueAdapter.updateList(it)
            }
        }

        if (viewModel.leagues.value.isNullOrEmpty()) {
            viewModel.getLeaguesList()
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
            val name = leagueNameEt.text.toString().trim()
            val image = leagueImageEt.text.toString().trim()
            val teams = leagueTeamsEt.text.toString().trim()
            val countryId = selectedCountryId

            if (name.isNotEmpty() && image.isNotEmpty() && teams.isNotEmpty() && !countryId.isNullOrEmpty()) {

                val newLeague = LeagueModel(
                    id = "",
                    leagueName = name,
                    leagueImage = image,
                    leagueTeams = teams,
                    countryId = countryId
                )

                viewModel.addLeague(newLeague) {
                    leagueNameEt.setText("")
                    leagueImageEt.setText("")
                    leagueTeamsEt.setText("")
                    countrySelector.setText("")
                    selectedCountryId = null
                    Toast.makeText(context, "Лига '${name}' успешно добавлена!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}