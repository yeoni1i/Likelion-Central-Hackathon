package com.example.atocuemobile.ui.screen.report.model

import java.time.DayOfWeek

// 요일별 긁음 총 횟수
data class WeeklyTrendData(
    val dayOfWeek: DayOfWeek,
    val count: Int
)