package com.example.atocuemobile.ui.screen.report.model

// 특정 시(hour)에 발생한 긁음 횟수
data class HourlyScratchData(
    val hour: Int,        // 1~24
    val count: Int        // 그 시간대(예: 1:00~1:59)에 발생한 긁음 횟수
)