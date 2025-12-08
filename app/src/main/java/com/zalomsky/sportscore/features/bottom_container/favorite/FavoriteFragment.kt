package com.zalomsky.sportscore.features.bottom_container.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.MatchResponseModel
import com.zalomsky.sportscore.domain.models.responses.ScheduleUiState
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        matchesRecyclerView = view.findViewById(R.id.matchesRecyclerView)
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar)
        errorTextView = view.findViewById(R.id.errorTextView)

        matchesAdapter = MatchesAdapter()
        matchesRecyclerView.adapter = matchesAdapter

        observeScheduleState()

        loadFavoriteSchedule()
    }

    fun loadFavoriteSchedule() {
        viewModel.loadFavoriteSchedule()
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
}