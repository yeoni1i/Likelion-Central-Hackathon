package com.example.atocuemobile.network.dto

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val token: String,
    val userId: Long,
    @SerializedName("isOnboarded")
    val isOnboarded: Boolean,
    val childId: Long?
)