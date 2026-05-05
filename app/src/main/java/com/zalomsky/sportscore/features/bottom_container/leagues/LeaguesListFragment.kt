package com.zalomsky.sportscore.features.bottom_container.leagues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.responses.LeagueResponseModel
import com.zalomsky.sportscore.domain.models.responses.LeaguesUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LeaguesListFragment : Fragment() {

    private val viewModel: LeaguesViewModel by viewModels()
    private lateinit var adapter: LeaguesAdapter
    private lateinit var progressBar: ProgressBar
    private var allLeagues: List<LeagueResponseModel> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_leagues_list, container, false)
        progressBar = view.findViewById(R.id.progressBar)
        val recyclerView = view.findViewById<RecyclerView>(R.id.leaguesRecyclerView)
        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)

        adapter = LeaguesAdapter { league ->
            val bundle = Bundle().apply {
                putString("leagueId", league.id)
            }
            findNavController().navigate(R.id.action_leaguesListFragment_to_leagueDetailFragment, bundle)
        }
        recyclerView.adapter = adapter

        searchEditText.addTextChangedListener { text ->
            filterLeagues(text.toString())
        }

        return view
    }

    private fun filterLeagues(query: String) {
        val filtered = if (query.isEmpty()) {
            allLeagues
        } else {
            allLeagues.filter { it.leagueName.contains(query, ignoreCase = true) }
        }
        adapter.submitList(filtered)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.leaguesState.onEach { state ->
            when (state) {
                is LeaguesUiState.Loading -> progressBar.visibility = View.VISIBLE
                is LeaguesUiState.Success -> {
                    progressBar.visibility = View.GONE
                    allLeagues = state.leagues
                    adapter.submitList(state.leagues)
                }
                is LeaguesUiState.Error -> {
                    progressBar.visibility = View.GONE
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}