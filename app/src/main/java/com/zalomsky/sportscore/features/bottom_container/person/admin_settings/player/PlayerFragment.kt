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
import com.zalomsky.sportscore.domain.models.PlayerModel
import com.zalomsky.sportscore.domain.models.SportType
import com.zalomsky.sportscore.domain.models.TeamModel
import com.zalomsky.sportscore.domain.models.responses.TeamResponseModel
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.league.LeagueAdapter
import com.zalomsky.sportscore.features.bottom_container.person.admin_settings.league.LeagueViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class PlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModels()
    private lateinit var playerAdapter: PlayerAdapter

    private var selectedTeamId: String? = null
    private var selectedSportType: String = SportType.FOOTBALL.name
    private var teamsList: List<TeamResponseModel> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.playersRecyclerView)
        val btnBack = view.findViewById<View>(R.id.btnBack)
        val playerSearchEt = view.findViewById<TextInputEditText>(R.id.playerSearchEditText)

        val fabAdd = view.findViewById<View>(R.id.fabAddPlayer)
        val addFormContainer = view.findViewById<View>(R.id.addPlayerFormContainer)
        val btnCancelAdd = view.findViewById<View>(R.id.btnCancelAdd)

        val playerNameEt = view.findViewById<TextInputEditText>(R.id.playerNameEditText)
        val playerImageEt = view.findViewById<TextInputEditText>(R.id.playerImageEditText)
        val playerPositionEt = view.findViewById<TextInputEditText>(R.id.playerPositionEditText)

        val teamSelector = view.findViewById<AutoCompleteTextView>(R.id.teamAutoCompleteTextView)
        val sportTypeSelector = view.findViewById<AutoCompleteTextView>(R.id.sportTypeAutoCompleteTextView)
        val addButton = view.findViewById<Button>(R.id.addPlayerButton)

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

        val sportAdapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            SportType.entries.map { it.name }
        ) {
            override fun getFilter(): android.widget.Filter {
                return object : android.widget.Filter() {
                    override fun performFiltering(constraint: CharSequence?): FilterResults {
                        val results = FilterResults()
                        results.values = SportType.entries.map { it.name }
                        results.count = SportType.entries.size
                        return results
                    }
                    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                        notifyDataSetChanged()
                    }
                }
            }
        }
        sportTypeSelector.setAdapter(sportAdapter)
        sportTypeSelector.setOnClickListener {
            sportTypeSelector.showDropDown()
        }
        sportTypeSelector.setText(selectedSportType, false)
        sportTypeSelector.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            selectedSportType = parent.getItemAtPosition(position).toString()
            // If TENNIS, hide team selector or clear it
            if (selectedSportType == SportType.TENNIS.name) {
                teamSelector.setText("", false)
                selectedTeamId = null
                view.findViewById<View>(R.id.teamSelectorLayout).visibility = View.GONE
            } else {
                view.findViewById<View>(R.id.teamSelectorLayout).visibility = View.VISIBLE
            }
        }

        playerAdapter = PlayerAdapter(emptyList())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playerAdapter
        }

        viewModel.getTeamsList()

        viewModel.players.observe(viewLifecycleOwner) { playersList ->
            playersList?.let {
                playerAdapter.updateList(it)
            }
        }

        viewModel.teams.observe(viewLifecycleOwner) { teams ->
            teamsList = teams
            if (teams.isNotEmpty()) {
                val teamNames = teams.map { it.teamName }
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    teamNames
                )
                teamSelector.setAdapter(adapter)
            }
        }

        teamSelector.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            selectedTeamId = teamsList.find { it.teamName == selectedName }?.teamId
        }

        addButton.setOnClickListener {
            val name = playerNameEt.text.toString().trim()
            val image = playerImageEt.text.toString().trim()
            val positionText = playerPositionEt.text.toString().trim()
            val teamId = selectedTeamId

            if (name.isNotEmpty() && image.isNotEmpty() && positionText.isNotEmpty()) {
                if (selectedSportType != SportType.TENNIS.name && teamId == null) {
                    Toast.makeText(context, "Выберите команду для футбола/хоккея", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val newPlayer = PlayerModel(
                    playerId = "", // Generated by server
                    playerName = name,
                    playerImage = image,
                    playerPosition = positionText,
                    teamId = teamId,
                    sportType = selectedSportType
                )

                viewModel.addPlayer(newPlayer) {
                    playerNameEt.setText("")
                    playerImageEt.setText("")
                    playerPositionEt.setText("")
                    teamSelector.setText("")
                    selectedTeamId = null
                    addFormContainer.visibility = View.GONE
                    fabAdd.visibility = View.VISIBLE
                    Toast.makeText(context, "Игрок '${name}' успешно добавлен!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Заполните основные поля!", Toast.LENGTH_SHORT).show()
            }
        }

        playerSearchEt.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                val filteredList = viewModel.players.value?.filter { it.playerName.lowercase().contains(query) } ?: emptyList()
                playerAdapter.updateList(filteredList)
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        if (viewModel.players.value.isNullOrEmpty()) {
            viewModel.getPlayersList()
        }
    }
}
