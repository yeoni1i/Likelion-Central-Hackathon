package com.example.atocuemobile.network.dto

import com.google.gson.annotations.SerializedName

data class DailyAnalysisResponse(
    @SerializedName("date")
    val date: String,
    @SerializedName("scratchCount")
    val scratchCount: Long,
    @SerializedName("peakHour")
    val peakHour: Int?,
    @SerializedName("hourly")
    val hourly: List<HourlyScratchDto>
)

data class HourlyScratchDto(
    @SerializedName("hour")
    val hour: Int,
    @SerializedName("count")
    val count: Long,
    @SerializedName("durationSec")
    val durationSec: Double? = 0.0
)