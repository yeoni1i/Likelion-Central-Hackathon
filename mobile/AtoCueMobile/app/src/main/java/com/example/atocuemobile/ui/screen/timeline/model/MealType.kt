package com.example.atocuemobile.ui.screen.timeline.model

enum class MealType(val label: String) {
    BREAKFAST("아침식사"),
    LUNCH("점심식사"),
    DINNER("저녁식사"),
    SNACK("간식")
    // TODO: "새로운 기록 추가"로 등록되는 항목은 이 enum 대신
    // 자유 입력 타입(String)으로 별도 처리 필요할 수 있음 (기획 확인)
}