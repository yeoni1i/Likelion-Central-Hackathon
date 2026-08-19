package com.example.atocuemobile.network.dto

data class PairingStatusResponse(
    val paired: Boolean,
    val deviceId: Long?
)