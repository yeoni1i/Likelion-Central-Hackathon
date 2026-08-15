package com.example.atocuemobile.ui.screen.timeline.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TimelineTopBar(
    onCalendarClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "타임라인",
            // TODO: 피그마 타이포그래피 스타일로 교체 (fontSize/weight)
        )
        IconButton(onClick = onCalendarClick) {
            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "달력 열기")
        }
    }
}