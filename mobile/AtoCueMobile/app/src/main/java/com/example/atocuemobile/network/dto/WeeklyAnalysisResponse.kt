package com.example.atocuemobile.network.dto

import com.google.gson.annotations.SerializedName

data class WeeklyAnalysisResponse(
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("endDate")
    val endDate: String,
    @SerializedName("totalCount")
    val totalCount: Long,
    @SerializedName("dailyAverage")
    val dailyAverage: Double,
    @SerializedName("daily")
    val daily: List<DailyScratchCountDto>
)

data class DailyScratchCountDto(
    @SerializedName("date")
    val date: String,
    @SerializedName("count")
    val count: Long,
    @SerializedName("dayOfWeek")
    val dayOfWeek: String? = null
)