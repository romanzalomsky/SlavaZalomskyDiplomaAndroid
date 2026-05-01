package com.zalomsky.sportscore.features.bottom_container.favorite

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.MatchResponseModel
import com.zalomsky.sportscore.domain.models.responses.ScheduleUiState
import com.zalomsky.sportscore.domain.models.responses.LeaguesUiState
import com.zalomsky.sportscore.domain.models.responses.LeagueResponseModel
import com.zalomsky.sportscore.features.bottom_container.games.GameViewModel
import com.zalomsky.sportscore.features.bottom_container.games.MatchesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private val viewModel: GameViewModel by viewModels()
    private lateinit var matchesAdapter: MatchesAdapter

    private lateinit var matchesRecyclerView: RecyclerView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var generatePdfFab: com.google.android.material.floatingactionbutton.FloatingActionButton
    private lateinit var sportSpinner: Spinner
    private lateinit var leagueSpinner: Spinner
    private var leagues: List<LeagueResponseModel> = emptyList()
    private var filteredLeagues: List<LeagueResponseModel> = emptyList()
    private var favoriteMatches: List<MatchResponseModel> = emptyList()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                generatePdfReport()
            } else {
                Toast.makeText(requireContext(), "Разрешение на сохранение PDF отклонено.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        matchesRecyclerView = view.findViewById(R.id.matchesRecyclerView)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        errorTextView = view.findViewById(R.id.errorTextView)
        generatePdfFab = view.findViewById(R.id.generatePdfFab)
        sportSpinner = view.findViewById(R.id.favoriteSportSpinner)
        leagueSpinner = view.findViewById(R.id.leagueSpinner)

        matchesAdapter = MatchesAdapter()

        matchesRecyclerView.layoutManager = LinearLayoutManager(context)
        matchesRecyclerView.adapter = matchesAdapter

        generatePdfFab.setOnClickListener {
            checkStoragePermissionAndGeneratePdf()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadFavoriteSchedule()
        setupSportSpinner()
        observeLeaguesState()
        observeScheduleState()
    }

    private fun setupSportSpinner() {
        val sports = listOf("ALL", "FOOTBALL", "TENNIS", "HOCKEY")
        val sportAdapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sports)
        sportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sportSpinner.adapter = sportAdapter
        sportSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                updateLeaguesFilter()
                applyFilters()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) = Unit
        }
    }

    private fun observeLeaguesState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.leaguesState.collect { state ->
                if (state is LeaguesUiState.Success) {
                    leagues = state.leagues
                    updateLeaguesFilter()
                }
            }
        }
    }

    private fun updateLeaguesFilter() {
        val selectedSport = sportSpinner.selectedItem?.toString() ?: "ALL"
        filteredLeagues = if (selectedSport == "ALL") leagues else leagues.filter { it.sportType.equals(selectedSport, true) }
        val leagueNames = mutableListOf("ALL").apply { addAll(filteredLeagues.map { it.leagueName }) }
        val adapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, leagueNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        leagueSpinner.adapter = adapter
        leagueSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                applyFilters()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) = Unit
        }
    }

    private fun applyFilters() {
        val selectedSport = sportSpinner.selectedItem?.toString() ?: "ALL"
        val leaguePosition = leagueSpinner.selectedItemPosition
        val leagueId = if (leaguePosition <= 0) "ALL" else filteredLeagues.getOrNull(leaguePosition - 1)?.id ?: "ALL"
        val leagueMap = leagues.associateBy { it.id }
        val filtered = favoriteMatches.filter { match ->
            val sportOk = if (selectedSport == "ALL") true else leagueMap[match.leagueId]?.sportType.equals(selectedSport, true)
            val leagueOk = leagueId == "ALL" || match.leagueId == leagueId
            sportOk && leagueOk
        }
        renderMatches(filtered)
    }

    private fun observeScheduleState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteScheduleState.collect { state ->
                when (state) {
                    is ScheduleUiState.Loading -> showLoading()
                    is ScheduleUiState.Success -> showData(state.matches)
                    is ScheduleUiState.Error -> showError(state.message)
                }
            }
        }
    }

    private fun showLoading() {
        loadingProgressBar.visibility = View.VISIBLE
        matchesRecyclerView.visibility = View.GONE
        errorTextView.visibility = View.GONE
    }

    private fun showData(matches: List<MatchResponseModel>) {
        favoriteMatches = matches
        renderMatches(matches)
    }

    private fun renderMatches(matches: List<MatchResponseModel>) {
        loadingProgressBar.visibility = View.GONE
        errorTextView.visibility = View.GONE

        if (matches.isEmpty()) {
            matchesRecyclerView.visibility = View.GONE
            errorTextView.apply {
                text = "У вас пока нет любимых матчей."
                visibility = View.VISIBLE
            }
        } else {
            matchesRecyclerView.visibility = View.VISIBLE
            matchesAdapter.submitList(matches)
        }
    }

    private fun showError(message: String) {
        loadingProgressBar.visibility = View.GONE
        matchesRecyclerView.visibility = View.GONE
        errorTextView.apply {
            text = "Ошибка: $message"
            visibility = View.VISIBLE
        }
    }

    private fun checkStoragePermissionAndGeneratePdf() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                generatePdfReport()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        } else {
            generatePdfReport()
        }
    }

    private fun generatePdfReport() {
        val matches = (viewModel.favoriteScheduleState.value as? ScheduleUiState.Success)?.matches

        if (matches.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Нет избранных матчей для отчета.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            PdfReportGenerator(requireContext()).createReport(matches)
            Toast.makeText(
                requireContext(),
                "PDF отчет создан и сохранен в папке Downloads!",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Log.e("FavoriteFragment", "Ошибка при создании PDF", e)

            // Если сообщение об ошибке null, показываем общее сообщение.
            val errorMessage = e.message ?: "Произошла внутренняя ошибка при сохранении файла. Проверьте Logcat."

            Toast.makeText(requireContext(), "Ошибка при создании PDF: $errorMessage", Toast.LENGTH_LONG).show()
        }
    }

}