package com.example.atocuemobile.ui.screen.report.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.network.dto.ScratchEventDto
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import com.example.atocuemobile.network.dto.HourlyScratchDto

// 시간대(새벽/오전/오후/저녁) - 시간 범위 매핑
private val timeSlotRanges = mapOf(
    "새벽" to 1..6,
    "오전" to 7..12,
    "오후" to 13..18,
    "저녁" to 19..24
)

// startTime 문자열에서 시(hour)를 뽑아, 0시는 24로 표기 (기존 목데이터 라벨 규칙과 동일)
private fun ScratchEventDto.hourLabelOrNull(): Int? {
    val raw = startTime ?: return null
    val hour = try {
        OffsetDateTime.parse(raw).hour
    } catch (e: DateTimeParseException) {
        try {
            LocalDateTime.parse(raw).hour
        } catch (e2: DateTimeParseException) {
            return null
        }
    }
    return if (hour == 0) 24 else hour
}

// 이벤트 리스트를 특정 시간대(range) 기준 "01" ~ "24" 카운트 리스트로 집계
private fun aggregateEventsBySlot(events: List<ScratchEventDto>, range: IntRange): List<Pair<String, Int>> {
    val counts = events.mapNotNull { it.hourLabelOrNull() }
        .filter { it in range }
        .groupingBy { it }
        .eachCount()
    return range.map { hour -> hour.toString().padStart(2, '0') to (counts[hour] ?: 0) }
}

private val defaultHourlyData = mapOf(
    "새벽" to listOf("01" to 5, "02" to 10, "03" to 2, "04" to 0, "05" to 3, "06" to 1),
    "오전" to listOf("07" to 12, "08" to 25, "09" to 15, "10" to 8, "11" to 4, "12" to 10),
    "오후" to listOf("13" to 18, "14" to 30, "15" to 42, "16" to 20, "17" to 15, "18" to 8),
    "저녁" to listOf("19" to 15, "20" to 40, "21" to 32, "22" to 22, "23" to 90, "24" to 28)
)

@Composable
fun HourlyScratchAnalysisSection(
    hourlyScratch: List<HourlyScratchDto>,
    pattern: String,
    carePoint: String
) {
    var selectedTimeSlot by remember { mutableStateOf("저녁") }
    val timeSlots = listOf("새벽", "오전", "오후", "저녁")

    val hourlyData = remember(selectedTimeSlot, hourlyScratch) {
        val range = timeSlotRanges.getValue(selectedTimeSlot)

        range.map { displayHour ->
            val actualHour = if (displayHour == 24) 0 else displayHour

            displayHour.toString().padStart(2, '0') to
                    (hourlyScratch.find { it.hour == actualHour }?.count ?: 0)
        }
    }

    val maxIndex = remember(hourlyData) { hourlyData.indices.maxByOrNull { hourlyData[it].second } ?: 0 }
    var selectedBarIndex by remember(hourlyData) { mutableStateOf(maxIndex) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .padding(bottom = 20.dp)
    ) {
        Text("내일은 이것을 확인해보세요!", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = carePoint, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(49.dp))

        Text("시간대별 긁음 분석", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text( text = pattern, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(25.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            timeSlots.forEach { slot ->
                val isSelected = slot == selectedTimeSlot
                Box(
                    modifier = Modifier
                        .size(width = 51.dp, height = 30.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (isSelected) Color(0xFF333333) else Color(0xFFF4F4F4))
                        .clickable { selectedTimeSlot = slot },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = slot,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        val maxCount = (hourlyData.maxOfOrNull { it.second } ?: 100).coerceAtLeast(1)

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.Bottom
            ) {
                hourlyData.forEachIndexed { index, (hour, count) ->
                    val isBarSelected = index == selectedBarIndex
                    val barHeightFraction = (count.toFloat() / maxCount.toFloat()).coerceIn(0.1f, 1.0f)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .width(36.dp)
                            .fillMaxHeight()
                            .clickable { selectedBarIndex = index }
                    ) {
                        if (isBarSelected) {
                            Box(
                                modifier = Modifier
                                    .size(width = 42.dp, height = 28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AtoCueBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${count}회", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        } else {
                            Spacer(modifier = Modifier.height(34.dp))
                        }

                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .fillMaxHeight(barHeightFraction * 0.7f)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(if (isBarSelected) AtoCueBlue else Color(0xFFE5E5E5))
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color(0xFFE5E5E5)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                hourlyData.forEach { (hour, _) ->
                    Box(
                        modifier = Modifier.width(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(hour, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 하단 요약 박스 — 아직 그대로 하드코딩 (그래프 범위 아님)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFECEEF2))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color(0xFF6C6E72), fontSize = 13.sp)) {
                            append("가장 길었던 긁음 반응  ")
                        }
                        withStyle(style = SpanStyle(color = Color(0xFF000000), fontWeight = FontWeight(400), fontSize = 13.sp)) {
                            append("약 18초")
                        }
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFECEEF2))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color(0xFF6C6E72), fontSize = 13.sp)) {
                            append("최근 3일 평균 대비,  ")
                        }
                        withStyle(style = SpanStyle(color = Color(0xFF000000), fontWeight = FontWeight(400), fontSize = 13.sp)) {
                            append("저녁시간대 20% 많음")
                        }
                    }
                )
            }
        }
    }
}