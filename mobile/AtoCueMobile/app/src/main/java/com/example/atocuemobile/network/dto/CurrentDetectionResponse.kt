package com.example.atocuemobile.network.dto

data class CurrentDetectionResponse(
    val deviceId: Long,
    val detectionStatus: String,
    val scratchStatus: String,
    val intensity: Int?,
    val lastScratchAt: String?
)