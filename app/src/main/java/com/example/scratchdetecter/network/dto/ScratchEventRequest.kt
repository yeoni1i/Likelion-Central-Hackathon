package com.example.scratchdetecter.network.dto

data class ScratchEventRequest(
    val clientEventId: String,
    val startTs: String,
    val endTs: String,
    val durationSec: Double,
    val intensity: Int,
    val confidence: Double,
    val windowCount: Int,
    val wearPosition: String
)
