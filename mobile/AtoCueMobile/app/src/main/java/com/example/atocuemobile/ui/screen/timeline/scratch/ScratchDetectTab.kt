package com.example.atocuemobile.ui.screen.timeline.scratch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.screen.timeline.MainBackGroundColor
import com.example.atocuemobile.ui.screen.timeline.model.ScratchEvent
import com.example.atocuemobile.ui.screen.timeline.model.ScratchLevel
import com.example.atocuemobile.ui.screen.timeline.scratch.component.ScratchHourSection
import java.time.LocalDate

@Composable
fun ScratchDetectTab(
    date: LocalDate
) {
    val events: List<ScratchEvent> = remember {
        listOf(
            ScratchEvent(java.time.LocalTime.of(1, 0), java.time.LocalTime.of(1, 15), 15, ScratchLevel.STABLE),
            ScratchEvent(java.time.LocalTime.of(1, 30), java.time.LocalTime.of(1, 40), 10, ScratchLevel.NORMAL),
            ScratchEvent(java.time.LocalTime.of(2, 5), java.time.LocalTime.of(2, 20), 15, ScratchLevel.CAUTION),
            ScratchEvent(java.time.LocalTime.of(4, 0), java.time.LocalTime.of(4, 10), 10, ScratchLevel.CAUTION),
            ScratchEvent(java.time.LocalTime.of(4, 20), java.time.LocalTime.of(4, 45), 25, ScratchLevel.DANGER)
        )
    }

    // 시간(0~23)별 이벤트 그룹핑
    val eventsByHour: Map<Int, List<ScratchEvent>> = remember(events) {
        events.groupBy { it.startTime.hour }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackGroundColor),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp)
    ) {
        item {
            Text(
                text = "오늘의 경고 타임라인",
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