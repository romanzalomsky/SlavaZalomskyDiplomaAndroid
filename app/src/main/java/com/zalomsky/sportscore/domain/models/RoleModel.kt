package com.zalomsky.sportscore.domain.models

import com.google.gson.annotations.SerializedName

enum class RoleModel {
    @SerializedName("ADMIN")
    ADMIN,
    @SerializedName("USER")
    USER
}
