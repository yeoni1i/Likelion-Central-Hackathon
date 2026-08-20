package com.example.atocuemobile.network.dto

data class DailyLogResponse(
    val id: Long,
    val mealType: String?,
    val foods: List<String> = emptyList(),
    val imageUrl: String?,
    val showerCount: Int?,
    val moisturizerCount: Int?,
    val symptoms: List<String> = emptyList(),
    val memo: String?,
    val date: String
)