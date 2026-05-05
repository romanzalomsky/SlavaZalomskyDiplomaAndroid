package com.zalomsky.sportscore.features.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.RegisterRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val viewModel: RegistrationViewModel by viewModels()

    private lateinit var regNameId: TextInputEditText
    private lateinit var authEmailId: TextInputEditText
    private lateinit var authPasswordId: TextInputEditText
    private lateinit var buttonAuth: Button
    private lateinit var authLink: TextView
    private lateinit var registerAsAdminCheckBox: CheckBox
    private lateinit var adminKeyLayout: View
    private lateinit var adminKeyId: TextInputEditText

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
        registerAsAdminCheckBox = view.findViewById(R.id.registerAsAdminCheckBox)
        adminKeyLayout = view.findViewById(R.id.adminKeyLayout)
        adminKeyId = view.findViewById(R.id.adminKeyId)

        registerAsAdminCheckBox.setOnCheckedChangeListener { _, checked ->
            adminKeyLayout.visibility = if (checked) View.VISIBLE else View.GONE
            if (!checked) adminKeyId.setText("")
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btnHeaderLogin).setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_authFragment)
        }

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

        val isAdminRegistration = registerAsAdminCheckBox.isChecked
        val adminKey = adminKeyId.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }
        if (isAdminRegistration && adminKey.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Для админ-регистрации нужен admin key", Toast.LENGTH_SHORT).show()
            return
        }

        val request = RegisterRequest(
            username = name,
            email = email,
            password = password,
            adminKey = adminKey
        )

        viewModel.createNewUser(
            request,
            isAdminRegistration,
            onSuccess = {
                findNavController().navigate(R.id.action_registerFragment_to_authFragment)
                Toast.makeText(requireContext(), "Вы успешно зарегистрированы", Toast.LENGTH_SHORT).show()
            },
            onError = {
                Toast.makeText(requireContext(), "Ошибка регистрации. Проверьте данные.", Toast.LENGTH_SHORT).show()
            })
    }
}