package com.zalomsky.sportscore.features.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.LoginRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var authButton: Button
    private lateinit var regLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)

        emailEditText = view.findViewById(R.id.regNameId)
        passwordEditText = view.findViewById(R.id.authPasswordId)
        authButton = view.findViewById(R.id.buttonAuth)
        regLink = view.findViewById(R.id.regLink)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        regLink.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_authFragment_to_registerFragment)
        }
        authButton.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Введите логин и пароль", Toast.LENGTH_SHORT).show()
            return
        }

        val loginRequest = LoginRequest(
            email = email,
            password = password
        )

        viewModel.getLogin(
            loginRequest,
            onSuccess = {
                findNavController().navigate(R.id.action_authFragment_to_homeFragment)
            },
            onError = {
                Toast.makeText(requireContext(), "Что с ебалом)))", Toast.LENGTH_SHORT).show()
            })
    }
}