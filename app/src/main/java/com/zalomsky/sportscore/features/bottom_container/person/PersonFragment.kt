package com.zalomsky.sportscore.features.bottom_container.person

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.RoleModel
import com.zalomsky.sportscore.features.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class PersonFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    private val ACTION_TO_ADMIN = R.id.action_personFragment_to_adminPersonFragment
    private val ACTION_TO_USER = R.id.action_personFragment_to_userPersonFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_person, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchRole()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userRole.collect { role ->

                role?.let {
                    navigateToRoleScreen(it)
                }

            }
        }
    }

    private fun navigateToRoleScreen(role: RoleModel) {
        val actionId = when (role) {
            RoleModel.ADMIN -> ACTION_TO_ADMIN
            RoleModel.USER -> ACTION_TO_USER
        }

        try {
            findNavController().navigate(actionId)
        } catch (e: Exception) {
            Log.e("PersonFragment", "Navigation error: ${e.message}")
        }
    }
}