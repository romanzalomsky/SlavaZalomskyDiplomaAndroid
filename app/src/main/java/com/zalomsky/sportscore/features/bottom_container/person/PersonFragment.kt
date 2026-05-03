package com.zalomsky.sportscore.features.bottom_container.person

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.RoleModel
import com.zalomsky.sportscore.features.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    private val ACTION_TO_ADMIN = R.id.action_personFragment_to_adminPersonFragment
    private val ACTION_TO_USER = R.id.action_personFragment_to_userPersonFragment
    private var hasNavigated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.getToken() == null) {
            navigateToAuth()
            return
        }

        viewModel.fetchRole()

        // Наблюдаем за ролью
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userRole.collect { role ->
                if (hasNavigated) return@collect
                val effectiveRole = role ?: viewModel.getSavedRole()
                effectiveRole?.let {
                    hasNavigated = true
                    navigateToRoleScreen(it)
                }
            }
        }

        // Наблюдаем за ошибками
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { errorMsg ->
                errorMsg?.let {
                    Log.e("PersonFragment", "Error received: $it")
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    
                    // Если произошла ошибка 400 или другая сетевая ошибка, 
                    // лучше отправить пользователя на экран логина через некоторое время
                    if (!hasNavigated) {
                        // navigateToAuth() // Опционально: раскомментируйте, чтобы выкидывать на логин при ошибке
                    }
                }
            }
        }
    }

    private fun navigateToRoleScreen(role: RoleModel) {
        try {
            val actionId = when (role) {
                RoleModel.ADMIN -> ACTION_TO_ADMIN
                RoleModel.USER -> ACTION_TO_USER
            }
            findNavController().navigate(actionId)
        } catch (e: Exception) {
            Log.e("PersonFragment", "Navigation error: ${e.message}")
        }
    }

    private fun navigateToAuth() {
        val options = navOptions {
            popUpTo(R.id.nav_graph) { inclusive = true }
        }
        // Используем findNavController активностей или основной NavHost
        try {
            requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.authFragment, null, options)
        } catch (e: Exception) {
            Log.e("PersonFragment", "Failed to navigate to auth: ${e.message}")
        }
    }
}
