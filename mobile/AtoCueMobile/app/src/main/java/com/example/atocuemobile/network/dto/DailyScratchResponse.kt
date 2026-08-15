package com.example.atocuemobile.network.dto

// 하루 기록 dto
data class DailyScratchResponse(
    val date: String,
    val eventCount: Int,
    val totalSeconds: Double,
    val averageIntensity: Double,
    val wearHours: Double,
    val scratchSecondsPerWearHour: Double
)