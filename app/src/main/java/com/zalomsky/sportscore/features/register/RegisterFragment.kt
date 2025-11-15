package com.zalomsky.sportscore.features.register

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
import com.zalomsky.sportscore.domain.models.RoleModel
import com.zalomsky.sportscore.domain.models.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val viewModel: RegistrationViewModel by viewModels()

    private lateinit var regNameId: TextInputEditText
    private lateinit var authEmailId: TextInputEditText
    private lateinit var authPasswordId: TextInputEditText
    private lateinit var buttonAuth: Button
    private lateinit var authLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        regNameId = view.findViewById(R.id.regNameId)
        authEmailId = view.findViewById(R.id.authEmailId)
        authPasswordId = view.findViewById(R.id.authPasswordId)
        buttonAuth = view.findViewById(R.id.buttonAuth)
        authLink = view.findViewById(R.id.authLink)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authLink.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_registerFragment_to_authFragment)
        }
        buttonAuth.setOnClickListener {
            performRegistration()
        }
    }

    private fun performRegistration() {

        val name = regNameId.text.toString().trim()
        val email = authEmailId.text.toString().trim()
        val password = authPasswordId.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(requireContext(), "Неправильное логин, имя или пароль", Toast.LENGTH_SHORT).show()
            return
        }

        val user = User(
            id = "",
            username = name,
            email = email,
            password = password,
            roleModel = RoleModel.USER
        )

        viewModel.createNewUser(
            user,
            onSuccess = {
                findNavController().navigate(R.id.action_registerFragment_to_authFragment)
                Toast.makeText(requireContext(), "Вы успешно зарегистрированы", Toast.LENGTH_SHORT).show()
            },
            onError = {
                Toast.makeText(requireContext(), "Что с ебалом)))", Toast.LENGTH_SHORT).show()
            })
    }
}