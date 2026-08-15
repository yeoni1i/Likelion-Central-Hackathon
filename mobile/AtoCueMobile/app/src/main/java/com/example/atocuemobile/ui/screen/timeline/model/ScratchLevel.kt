package com.example.atocuemobile.ui.screen.timeline.model

// 긁음 강도 레벨. 피그마상 안정/보통/주의/위험 4단계
enum class ScratchLevel(val label: String) {
    STABLE("안정"),
    NORMAL("보통"),
    CAUTION("주의"),
    DANGER("위험")
    // TODO: 색상/아이콘은 각 화면에서 when으로 매핑 (아래 ScratchEventItem 참고)
}