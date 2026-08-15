package com.example.atocuemobile.network.dto

// 이미 다른 파일에 있는 LoginRequest, PairingCodeResponse 등은 제외하고 없는 것만 선언
data class PairDeviceRequest(
    val pairingCode: String,
    val deviceId: String,
    val deviceName: String
)

data class PairDeviceResponse(
    val success: Boolean,
    val deviceId: String?,
    val message: String?
)