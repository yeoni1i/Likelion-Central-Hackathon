package com.example.scratchdetecter.network

data class PairDeviceRequest(
    val pairingCode: String,
    val deviceId: String,
    val deviceName: String
)