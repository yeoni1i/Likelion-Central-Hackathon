package com.example.atocuemobile.network.dto

import com.google.gson.annotations.SerializedName

// 쿼리 대신 바디로 전달해야 할 경우를 대비한 요청 DTO
data class CreatePairingCodeRequest(
    @SerializedName("parentUserId")
    val parentUserId: Long
)

// 1. 워치 페어링 6자리 코드 발급 응답 DTO
data class PairingCodeResponse(
    @SerializedName("pairingCode")
    val pairingCode: String,
    @SerializedName("expiresAt")
    val expiresAt: String? = null
)

// 2. 워치 기기 등록 요청 DTO
data class PairDeviceRequest(
    @SerializedName("pairingCode")
    val pairingCode: String,
    @SerializedName("deviceId")
    val deviceId: String,
    @SerializedName("deviceName")
    val deviceName: String
)

// 3. 워치 기기 등록 응답 DTO
data class PairDeviceResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("deviceId")
    val deviceId: String?,
    @SerializedName("message")
    val message: String?
)