package com.example.atocuemobile.ui.screen.report.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.screen.report.model.HourlyScratchData
import com.example.atocuemobile.ui.screen.report.model.TimeOfDay

@Composable
fun HourlyAnalysisSection(
    hourlyData: List<HourlyScratchData>,   // 하루 전체(1~24시) 데이터
    peakHour: Int?,                        // "저녁 23시에서 24시 사이" 문구 계산용
    selectedTimeOfDay: TimeOfDay,
    onTimeOfDaySelect: (TimeOfDay) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Text(text = "내일은 이것을 확인해보세요!", fontSize = 14.sp, color = Color.Gray)
        // TODO: "내일은 이것을 확인해보세요!" 밑에 실제 안내 문구(오늘은 건조한 환경과...)도
        // 필요하면 여기 Text 하나 추가

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "시간대별 긁음 분석", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = peakHour?.let { "${timeLabel(it)}에서 ${timeLabel(it + 1)} 사이에 집중적으로 발생했어요." }
                ?: "긁음 기록이 없어요.",
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 새벽/오전/오후/저녁 토글
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF0F0F2))
                .padding(4.dp)
        ) {
            TimeOfDay.entries.forEach { option ->
                val isSelected = option == selectedTimeOfDay
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color.Black else Color.Transparent)
                        .clickable { onTimeOfDaySelect(option) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = option.label,
                        fontSize = 13.sp,
                        color = if (isSelected) Color.White else Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 막대그래프 (선택된 시간대 구간만)
        val filteredData = hourlyData.filter { it.hour in selectedTimeOfDay.hourRange }
        val maxCount = (filteredData.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)

        Row(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            selectedTimeOfDay.hourRange.forEach { hour ->
                val count = filteredData.find { it.hour == hour }?.count ?: 0
                val isPeak = hour == peakHour
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isPeak && count > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF5398FF))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(text = "${count}회", fontSize = 11.sp, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Box(
                        modifier = Modifier
                            .width(20.dp)                                     // TODO: 막대 너비 조절
                            .height((count.toFloat() / maxCount * 100).dp.coerceAtLeast(4.dp))
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isPeak) Color(0xFF5398FF) else Color(0xFFE5E5EA))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = hour.toString(), fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}

private fun timeLabel(hour: Int): String {
    val h = if (hour > 24) hour - 24 else hour
    return "${h}시"
}