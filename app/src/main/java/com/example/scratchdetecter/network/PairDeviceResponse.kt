package com.example.scratchdetecter.network

data class PairDeviceResponse(
    val success: Boolean,
    val deviceId: String,
    val message: String
)