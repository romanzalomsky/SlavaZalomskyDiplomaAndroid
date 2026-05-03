package com.zalomsky.sportscore.features.bottom_container.person.admin_settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.data.UserRepositoryImpl
import com.zalomsky.sportscore.features.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.fragment.app.viewModels
import javax.inject.Inject

@AndroidEntryPoint
class AdminPersonFragment : Fragment() {

    @Inject
    lateinit var userRepository: UserRepositoryImpl

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var usersAdapter: AdminUsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingsContent = view.findViewById<View>(R.id.settingsContent)
        val usersRecyclerView = view.findViewById<RecyclerView>(R.id.adminUsersRecyclerView)
        val toggleGroup = view.findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup)

        val linkToCountry = view.findViewById<View>(R.id.linkToCountryButton)
        val linkToCity = view.findViewById<View>(R.id.linkToCityButton)
        val linkToLeague = view.findViewById<View>(R.id.linkToLeagueButton)
        val linkToPlayer = view.findViewById<View>(R.id.linkToPlayerButton)
        val linkToTeam = view.findViewById<View>(R.id.linkToTeamButton)
        val logoutButton = view.findViewById<View>(R.id.logoutAdminButton)

        usersAdapter = AdminUsersAdapter(emptyList()) { user ->
            user.id?.let { deleteUser(it) }
        }
        usersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        usersRecyclerView.adapter = usersAdapter

        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnSettingsTab -> {
                        settingsContent.isVisible = true
                        usersRecyclerView.isVisible = false
                    }
                    R.id.btnUsersTab -> {
                        settingsContent.isVisible = false
                        usersRecyclerView.isVisible = true
                        loadUsers()
                    }
                }
            }
        }

        linkToCountry.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminPersonFragment_to_countryFragment)
        }
        linkToCity.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminPersonFragment_to_cityFragment)
        }
        linkToLeague.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminPersonFragment_to_leagueFragment)
        }
        linkToPlayer.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminPersonFragment_to_playerFragment)
        }
        linkToTeam.setOnClickListener {
            it.findNavController().navigate(R.id.action_adminPersonFragment_to_teamFragment2)
        }
        logoutButton.setOnClickListener {
            authViewModel.logout()
            val options = navOptions { popUpTo(R.id.nav_graph) { inclusive = true } }
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.authFragment, null, options)
        }
    }

    private fun loadUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching { userRepository.getAllUsers() }
                .onSuccess { users ->
                    withContext(Dispatchers.Main) {
                        usersAdapter.submitList(users)
                    }
                }
                .onFailure {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Ошибка загрузки пользователей", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun deleteUser(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching { userRepository.deleteUser(userId) }
                .onSuccess { response ->
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Пользователь удален", Toast.LENGTH_SHORT).show()
                            loadUsers()
                        } else {
                            Toast.makeText(requireContext(), "Удаление не удалось", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .onFailure {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Ошибка удаления пользователя", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
