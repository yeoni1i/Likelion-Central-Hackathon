package com.example.atocuemobile.ui.screen.report.model

// 시간대별 토글 (새벽/오전/오후/저녁)
enum class TimeOfDay(val label: String, val hourRange: IntRange) {
    DAWN("새벽", 1..6),
    MORNING("오전", 7..12),
    AFTERNOON("오후", 13..18),
    EVENING("저녁", 19..24)
    // TODO: 0시(자정)를 어디 구간에 넣을지 기획 확인 필요.
    // 지금은 1~24로 처리, 0시는 24시와 같이 취급하거나 별도 처리 필요할 수 있음
}
