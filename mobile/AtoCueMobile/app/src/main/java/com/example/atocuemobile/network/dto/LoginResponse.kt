package com.example.atocuemobile.network.dto

data class LoginResponse(
    val token: String,
    val userId: Long,
    val isOnboarded: Boolean
)