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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.Country
import com.zalomsky.sportscore.domain.models.LeagueModel
import com.zalomsky.sportscore.domain.models.SportType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeagueFragment : Fragment() {

    private val viewModel: LeagueViewModel by viewModels()
    private lateinit var leagueAdapter: LeagueAdapter

    private var selectedCountryId: String? = null
    private var selectedSportType: String = SportType.FOOTBALL.name
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
        val btnBack = view.findViewById<View>(R.id.btnBack)
        val leagueSearchEt = view.findViewById<TextInputEditText>(R.id.leagueSearchEditText)
        val fabAdd = view.findViewById<View>(R.id.fabAddLeague)
        val addFormContainer = view.findViewById<View>(R.id.addLeagueFormContainer)
        val btnCancelAdd = view.findViewById<View>(R.id.btnCancelAdd)

        val leagueNameEt = view.findViewById<TextInputEditText>(R.id.leagueNameEditText)
        val leagueImageEt = view.findViewById<TextInputEditText>(R.id.leagueImageEditText)

        val countrySelector = view.findViewById<AutoCompleteTextView>(R.id.countryAutoCompleteTextViewLeague)
        val sportTypeSelector = view.findViewById<AutoCompleteTextView>(R.id.sportTypeAutoCompleteTextView)
        val addButton = view.findViewById<Button>(R.id.addLeagueButton)

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

        val sportAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            SportType.values().map { it.name }
        )
        sportTypeSelector.setAdapter(sportAdapter)
        sportTypeSelector.setText(selectedSportType, false)
        sportTypeSelector.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            selectedSportType = parent.getItemAtPosition(position).toString()
        }


        leagueAdapter = LeagueAdapter(
            emptyList(),
            onItemClick = { leagueId ->
                findNavController().navigate(
                    LeagueFragmentDirections.actionLeagueFragmentToLeagueDetailsFragment(leagueId)
                )
            }

        )


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

        leagueSearchEt.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                val filteredList = viewModel.leagues.value?.filter { it.leagueName.lowercase().contains(query) } ?: emptyList()
                leagueAdapter.updateList(filteredList)
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

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
            val countryId = selectedCountryId

            if (name.isNotEmpty() && image.isNotEmpty() && !countryId.isNullOrEmpty()) {

                val newLeague = LeagueModel(
                    id = "",
                    leagueName = name,
                    leagueImage = image,
                    countryId = countryId,
                    sportType = selectedSportType
                )

                viewModel.addLeague(newLeague) {
                    leagueNameEt.setText("")
                    leagueImageEt.setText("")
                    countrySelector.setText("")
                    sportTypeSelector.setText(SportType.FOOTBALL.name, false)
                    selectedSportType = SportType.FOOTBALL.name
                    selectedCountryId = null
                    addFormContainer.visibility = View.GONE
                    fabAdd.visibility = View.VISIBLE
                    Toast.makeText(context, "Лига '${name}' успешно добавлена!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}