package com.example.scratchdetecter.network.dto

data class PairDeviceRequest(
    val pairingCode: String,
    val deviceId: String,
    val deviceName: String
)
