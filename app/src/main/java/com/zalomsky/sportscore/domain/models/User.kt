package com.zalomsky.sportscore.domain.models

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class User(
    @SerializedName("id") val id: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("password") val password: String? = null,
    // Основной ключ "role", так как он используется в ответе сервера (mapOf("role" to ...))
    @SerializedName("role", alternate = ["user_role", "userRole"])
    val roleModel: RoleModel? = RoleModel.USER
)
