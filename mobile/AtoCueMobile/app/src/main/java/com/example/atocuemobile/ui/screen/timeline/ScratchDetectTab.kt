package com.example.atocuemobile.ui.screen.timeline

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// TODO: 상태값(안정/보통/주의/위험)에 따라 색상/이모지 다르게 표시
data class ScratchEvent(
    val time: String,        // 예: "01:00 AM"
    val status: String,      // 예: "안정", "보통", "주의", "위험"
    val duration: String,    // 예: "00분 지속"
    val timeRange: String    // 예: "발생시각 | 00:00~00:00"
)

@Composable
fun ScratchDetectTab(
    events: List<ScratchEvent> = emptyList()  // TODO: 실제 데이터 연동
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("오늘의 경고 타임라인")

        // TODO: events.forEach로 실제 렌더링. 지금은 구조만
        events.forEach { event ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(event.time, modifier = Modifier.padding(end = 12.dp))
                Column {
                    Text(event.status)
                    Text(event.timeRange)
                }
                Text(event.duration)
            }
        }

        if (events.isEmpty()) {
            Text("긁음 기록 없음")
        }
    }
}