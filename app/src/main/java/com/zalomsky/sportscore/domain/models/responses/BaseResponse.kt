package com.zalomsky.sportscore.domain.models.responses

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class BaseResponse (
    val success: Boolean,
    val message: String? = null,
)