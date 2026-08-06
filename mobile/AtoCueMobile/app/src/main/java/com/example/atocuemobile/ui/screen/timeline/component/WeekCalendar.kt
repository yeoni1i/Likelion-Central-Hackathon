package com.example.atocuemobile.ui.screen.timeline.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun WeekCalendar(
    month: String,             // 예: "7월"
    selectedDate: Int,         // 예: 24
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelect: (Int) -> Unit
    // TODO: 실제 주간 날짜 리스트(일~토)는 데이터로 받아서 forEach 렌더링
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "타임라인")
        IconButton(onClick = { /* TODO: 캘린더 팝업 열기 (이미지 2번째처럼) */ }) {
            Icon(Icons.Default.CalendarMonth, contentDescription = "캘린더")
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = month)
        // TODO: 요일 7개(일~토) Row로 나열, selectedDate와 일치하는 항목 하이라이트
    }
}