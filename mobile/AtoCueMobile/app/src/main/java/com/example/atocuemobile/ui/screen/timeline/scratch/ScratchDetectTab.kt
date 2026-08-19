package com.example.atocuemobile.ui.screen.timeline.scratch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.model.ScratchStatus
import com.example.atocuemobile.ui.screen.timeline.MainBackGroundColor
import com.example.atocuemobile.ui.screen.timeline.model.ScratchEvent
import com.example.atocuemobile.ui.screen.timeline.model.ScratchLevel
import com.example.atocuemobile.ui.screen.timeline.scratch.component.ScratchHourSection
import com.example.atocuemobile.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ScratchDetectTab(
    date: LocalDate,
    homeViewModel: HomeViewModel
) {
    val uiState by homeViewModel.uiState.collectAsState()

    LaunchedEffect(date) {
        homeViewModel.loadDate(date)
    }

    val events: List<ScratchEvent> = remember(uiState.timelineItems) {
        uiState.timelineItems.mapNotNull { item ->

            val timeText = item.timeRangeLabel
            val parts = timeText.split("~")

            if (parts.size != 2) {
                return@mapNotNull null
            }

            val startTime = runCatching {
                LocalTime.parse(
                    parts[0],
                    DateTimeFormatter.ofPattern("HH:mm")
                )
            }.getOrNull() ?: return@mapNotNull null

            val endTime = runCatching {
                LocalTime.parse(
                    parts[1],
                    DateTimeFormatter.ofPattern("HH:mm")
                )
            }.getOrNull() ?: return@mapNotNull null

            val durationMinutes = item.durationLabel
                .replace("분", "")
                .trim()
                .toIntOrNull()
                ?: 0

            val level = when (item.status) {
                ScratchStatus.STABLE -> ScratchLevel.STABLE
                ScratchStatus.NORMAL -> ScratchLevel.NORMAL
                ScratchStatus.WARNING -> ScratchLevel.CAUTION
                ScratchStatus.DANGER -> ScratchLevel.DANGER
                ScratchStatus.VERY_DANGER -> ScratchLevel.DANGER
            }

            ScratchEvent(
                startTime = startTime,
                endTime = endTime,
                durationMinutes = durationMinutes,
                level = level
            )
        }
    }

    val eventsByHour: Map<Int, List<ScratchEvent>> =
        remember(events) {
            events.groupBy { it.startTime.hour }
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackGroundColor),
        contentPadding = PaddingValues(
            horizontal = 24.dp,
            vertical = 24.dp
        )
    ) {
        item {
            Text(
                text = if (date == LocalDate.now()) {
                    "오늘의 경고 타임라인"
                } else {
                    "${date.monthValue}월 ${date.dayOfMonth}일 경고 타임라인"
                },
                fontSize = 20.sp,
                fontWeight = FontWeight(600)
            )

            Spacer(modifier = Modifier.height(18.dp))
        }

        items((0..23).toList()) { hour ->
            ScratchHourSection(
                hour = hour,
                events = eventsByHour[hour] ?: emptyList()
            )
        }
    }
}