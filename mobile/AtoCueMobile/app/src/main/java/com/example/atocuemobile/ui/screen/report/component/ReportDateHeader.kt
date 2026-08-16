package com.example.atocuemobile.ui.screen.report.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun ReportDateHeader(
    selectedDate: LocalDate,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Text(
            text = "일간 리포트",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
            // TODO: 정확한 크기/두께는 피그마 값으로 교체
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevDay) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "이전 날짜")
            }
            Text(
                text = formatReportDateLabel(selectedDate),
                fontSize = 14.sp
            )
            IconButton(onClick = onNextDay) {
                Icon(Icons.Default.ChevronRight, contentDescription = "다음 날짜")
            }
        }
    }
}

private fun formatReportDateLabel(date: LocalDate): String {
    val today = LocalDate.now()
    val suffix = if (date == today) ", 오늘" else ""
    val dayOfWeekKor = listOf("월", "화", "수", "목", "금", "토", "일")[date.dayOfWeek.value - 1]
    return "${date.monthValue}월 ${date.dayOfMonth}일 (${dayOfWeekKor}요일)$suffix"
}