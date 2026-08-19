package com.example.atocuemobile.ui.screen.timeline.model

import java.time.LocalDate

data class MealRecord(
    val date: LocalDate,
    val mealType: MealType,
    val photoUrl: String?,      // null이면 "아직 등록된 기록이 없습니다"
    val menuItems: List<String> = emptyList()
)