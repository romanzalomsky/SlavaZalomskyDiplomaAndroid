package com.zalomsky.sportscore.domain.models

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val adminKey: String? = null
)
