package com.example.atocuemobile.network.dto

data class DailyAnalysisReportResponse(
    val date: String,
    val scratchSummary: ScratchSummaryDto,
    val environment: EnvironmentDto,
    val hourlyScratch: List<HourlyScratchDto>,
    val weeklyTrend: List<WeeklyTrendDto>,
    val analysis: AnalysisDto
)

data class ScratchSummaryDto(
    val count: Int,
    val totalSeconds: Double,
    val weeklyAverage: Double,
    val changePercent: Double
)

data class EnvironmentDto(
    val temperature: Double?,
    val humidity: Int?,
    val airQuality: String?
)


data class WeeklyTrendDto(
    val date: String,
    val count: Int
)

data class AnalysisDto(
    val summary: String,
    val pattern: String,
    val carePoint: String,
    val triggerFactor: String?,
    val triggerFactors: List<TriggerFactorDto> = emptyList()
)
data class TriggerFactorDto(
    val rank: Int,
    val type: String,
    val factor: String,
    val reason: String
)