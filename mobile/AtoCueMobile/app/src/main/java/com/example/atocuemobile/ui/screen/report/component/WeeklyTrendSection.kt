package com.example.atocuemobile.ui.screen.report.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.screen.report.model.WeeklyTrendData
import java.time.DayOfWeek

@Composable
fun WeeklyTrendSection(
    weeklyData: List<WeeklyTrendData>,     // 일~토 7개
    averageCount: Int
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Text(text = "주간 추이", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = weeklyTrendMessage(weeklyData, averageCount),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        val maxCount = (weeklyData.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)
        val peakDay = weeklyData.maxByOrNull { it.count }

        Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
            // 평균선 (빨간 점선)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val chartHeight = size.height - 24.dp.toPx()   // 하단 요일 라벨 공간 제외
                val lineY = chartHeight - (averageCount.toFloat() / maxCount * chartHeight)
                drawLine(
                    color = Color.Red,
                    start = Offset(0f, lineY),
                    end = Offset(size.width, lineY),
                    strokeWidth = 2.dp.toPx()
                    // TODO: 점선으로 하려면 pathEffect = PathEffect.dashPathEffect(...) 사용
                )
            }

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyData.forEach { data ->
                    val isPeak = data == peakDay
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isPeak) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF5398FF))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(text = "총 ${data.count}회", fontSize = 11.sp, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height((data.count.toFloat() / maxCount * 120).dp.coerceAtLeast(4.dp))
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isPeak) Color(0xFF5398FF) else Color(0xFFE5E5EA))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = dayLabel(data.dayOfWeek), fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val today = weeklyData.lastOrNull() // TODO: 실제 "오늘"에 해당하는 데이터로 교체
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F7))
                .padding(16.dp)
        ) {
            Text(text = "최근 7일 중 오늘 긁음이 가장 많았어요.", fontSize = 13.sp)
            // TODO: 실제 데이터 기반으로 동적 문구 생성 필요
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "최근 일주일 평균보다 18% 증가했어요.", fontSize = 13.sp)
        }
    }
}

private fun dayLabel(day: DayOfWeek): String = when (day) {
    DayOfWeek.SUNDAY -> "일"
    DayOfWeek.MONDAY -> "월"
    DayOfWeek.TUESDAY -> "화"
    DayOfWeek.WEDNESDAY -> "수"
    DayOfWeek.THURSDAY -> "목"
    DayOfWeek.FRIDAY -> "금"
    DayOfWeek.SATURDAY -> "토"
}

private fun weeklyTrendMessage(data: List<WeeklyTrendData>, average: Int): String {
    // TODO: 실제 로직으로 "심해졌어요"/"완화됐어요" 등 판단해서 문구 생성
    return "최근 7일보다\n증상이 심해졌어요"
}