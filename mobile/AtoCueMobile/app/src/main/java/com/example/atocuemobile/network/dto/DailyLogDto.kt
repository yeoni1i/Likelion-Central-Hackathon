package com.example.atocuemobile.network.dto

// 서버에 일상/식단 기록을 보낼 때 사용하는 요청 데이터
data class DailyLogCreateRequest(
    val mealType: String,       // BREAKFAST, LUNCH, DINNER, SNACK
    val foods: List<String>,
    val showerCount: Int,
    val moisturizerCount: Int,
    val symptoms: List<String>,
    val memo: String,
    val date: String            // "YYYY-MM-DD"
)

// 서버에서 응답으로 받는 데이터
data class DailyLogResponse(
    val id: Long,
    val mealType: String,
    val foods: List<String>,
    val imageUrl: String?,
    val showerCount: Int,
    val moisturizerCount: Int,
    val symptoms: List<String>,
    val memo: String,
    val date: String
)