package com.example.atocuemobile.ui.screen.timeline.model

enum class ShowerCount(val label: String, val count: Int) {
    ONCE("1회", 1),
    TWICE("2회", 2),
    THREE_OR_MORE("3회 이상", 3)
    // TODO: 실제 "3회 이상"이 4회, 5회처럼 더 늘어날 수 있는지 기획 확인 필요.
    // 지금은 3개 버튼 중 하나만 선택하는 구조로 가정
}

data class LifeRecord(
    val showerCount: ShowerCount = ShowerCount.THREE_OR_MORE,
    val moisturizerCount: Int = 0,  // 1~20회 슬라이더
    val symptoms: List<SymptomType> = emptyList(),
    val note: String = ""
)