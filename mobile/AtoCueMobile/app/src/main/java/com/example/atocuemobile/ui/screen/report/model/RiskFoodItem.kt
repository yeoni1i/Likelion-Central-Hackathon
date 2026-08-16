package com.example.atocuemobile.ui.screen.report.model

// 위험 식단 리스트 항목 (사진4)
data class RiskFoodItem(
    val reactionCount: Int,     // "누적 반응 5회"의 5
    val foodNames: String       // "유제품, 밀가루" 등
)