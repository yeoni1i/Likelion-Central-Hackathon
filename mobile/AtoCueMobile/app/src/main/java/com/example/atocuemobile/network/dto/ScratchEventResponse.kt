package com.example.atocuemobile.network.dto
//긁음 기록 dto

data class ScratchEventResponse(
    val eventId: Long?,
    val startTs: String,
    val endTs: String?,
    val durationSec: Double,
    val intensity: Int?,
    val confidence: Double?
)