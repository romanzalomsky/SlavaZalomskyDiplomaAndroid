package com.zalomsky.sportscore.domain.models

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class User(

    val id: String,
    val username: String,
    val email: String,
    val password: String,
    val roleModel: RoleModel
)