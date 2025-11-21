package com.zalomsky.sportscore.features.bottom_container.person.admin_settings.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.Country
import com.zalomsky.sportscore.domain.models.LeagueModel
import com.zalomsky.sportscore.domain.models.PlayerModel
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.league.LeagueAdapter
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.league.LeagueViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class PlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModels()
    private lateinit var playerAdapter: PlayerAdapter

    private var selectedCountryId: String? = null
    private var countriesList: List<Country> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.playersRecyclerView)

        val playerNameEt = view.findViewById<TextInputEditText>(R.id.playerNameEditText)
        val playerImageEt = view.findViewById<TextInputEditText>(R.id.playerImageEditText)
        val playerPositionEt = view.findViewById<TextInputEditText>(R.id.playerPositionEditText)

        val countrySelector = view.findViewById<AutoCompleteTextView>(R.id.countryAutoCompleteTextViewPlayer)
        val addButton = view.findViewById<Button>(R.id.addPlayerButton)

        playerAdapter = PlayerAdapter(emptyList())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playerAdapter
        }

        viewModel.getCountriesList()

        viewModel.players.observe(viewLifecycleOwner) { playersList ->
            playersList?.let {
                playerAdapter.updateList(it)
            }
        }

        if (viewModel.players.value.isNullOrEmpty()) {
            viewModel.getPlayersList()
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
            val name = playerNameEt.text.toString().trim()
            val image = playerImageEt.text.toString().trim()
            val position = playerPositionEt.text.toString().trim()
            val countryId = selectedCountryId

            if (name.isNotEmpty() && image.isNotEmpty() && position.isNotEmpty() && !countryId.isNullOrEmpty()) {

                val newPlayer = PlayerModel(
                    playerId = "",
                    playerName = name,
                    playerImage = image,
                    playerPosition = position,
                    countryId = countryId
                )

                viewModel.addPlayer(newPlayer) {
                    playerNameEt.setText("")
                    playerImageEt.setText("")
                    playerPositionEt.setText("")
                    countrySelector.setText("")
                    selectedCountryId = null
                    Toast.makeText(context, "Игрок '${name}' успешно добавлен!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}