package com.example.atocuemobile.network.dto

data class DailyLogCreateRequest(
    val mealType: String? = null,
    val foods: List<String> = emptyList(),
    val showerCount: Int? = null,
    val moisturizerCount: Int? = null,
    val symptoms: List<String> = emptyList(),
    val memo: String? = null,
    val date: String
)
