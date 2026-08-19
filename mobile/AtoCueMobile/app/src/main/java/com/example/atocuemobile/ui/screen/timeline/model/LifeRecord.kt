package com.example.atocuemobile.ui.screen.timeline.model

enum class ShowerCount(val label: String, val count: Int) {
    ONCE("1회", 1),
    TWICE("2회", 2),
    THREE_OR_MORE("3회 이상", 3)
}

data class LifeRecord(
    val showerCount: ShowerCount = ShowerCount.THREE_OR_MORE,
    val moisturizerCount: Int = 0,
    val symptoms: List<SymptomType> = emptyList<SymptomType>(),
    val note: String = ""
)