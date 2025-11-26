package com.zalomsky.sportscore.features.bottom_container.favorite

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.zalomsky.sportscore.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private val viewModel: FavoriteViewModel by viewModels()

    private lateinit var searchEditText: android.widget.EditText
    private lateinit var searchResultsRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var favoriteResultsRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var favoriteListTitleTextView: android.widget.TextView

    private lateinit var searchAdapter: FavoriteSearchTeamAdapter
    private lateinit var favoriteAdapter: FavoriteTeamAdapter
    private var searchJob: Job? = null
    private val SEARCH_DELAY_MS = 500L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        searchEditText = view.findViewById(R.id.searchEditText)
        searchResultsRecyclerView = view.findViewById(R.id.teamSearchResultsRecyclerView)
        favoriteResultsRecyclerView = view.findViewById(R.id.favoriteTeamsRecyclerView)
        favoriteListTitleTextView = view.findViewById(R.id.favoriteListTitleTextView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteAdapter = FavoriteTeamAdapter(emptyList())
        setupSearchRecyclerView()

        favoriteResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoriteAdapter
        }

        setupSearchListener()
        observeViewModel()

        viewModel.loadFavoriteTeams()
    }

    private fun setupSearchRecyclerView() {
        searchAdapter = FavoriteSearchTeamAdapter(
            teams = emptyList(),
            onFavoriteClick = { team ->
                viewModel.addTeamToFavorites(team.teamId)
                searchEditText.setText("")
            })
        searchResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }
    }

    private fun setupSearchListener() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()

                searchJob?.cancel()

                if (query.length >= 2) {
                    searchJob = MainScope().launch {
                        delay(SEARCH_DELAY_MS)
                        viewModel.searchTeamsSimple(query)
                    }
                } else {
                    searchAdapter.updateList(emptyList())
                    searchResultsRecyclerView.visibility = View.GONE
                    favoriteListTitleTextView.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { teams ->
            if (searchEditText.text.toString().trim().length < 2) {
                return@observe
            }

            searchAdapter.updateList(teams)

            if (teams.isNotEmpty()) {
                searchResultsRecyclerView.visibility = View.VISIBLE
                favoriteListTitleTextView.visibility = View.GONE
            } else {
                searchResultsRecyclerView.visibility = View.GONE

                favoriteListTitleTextView.visibility = View.VISIBLE
            }
        }

        viewModel.favoriteTeams.observe(viewLifecycleOwner) { teamList ->

            favoriteAdapter.updateList(teamList)

            if (searchEditText.text.toString().trim().isEmpty()) {
                favoriteListTitleTextView.visibility = View.VISIBLE
            }
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
    }
}