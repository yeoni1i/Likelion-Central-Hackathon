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
import com.example.atocuemobile.ui.screen.report.AtoCueBlue

@Composable
fun HourlyScratchAnalysisSection() {
    var selectedTimeSlot by remember { mutableStateOf("저녁") }
    val timeSlots = listOf("새벽", "오전", "오후", "저녁")

    val hourlyData = remember(selectedTimeSlot) {
        when (selectedTimeSlot) {
            "새벽" -> listOf("01" to 5, "02" to 10, "03" to 2, "04" to 0, "05" to 3, "06" to 1)
            "오전" -> listOf("07" to 12, "08" to 25, "09" to 15, "10" to 8, "11" to 4, "12" to 10)
            "오후" -> listOf("13" to 18, "14" to 30, "15" to 42, "16" to 20, "17" to 15, "18" to 8)
            else -> listOf("19" to 15, "20" to 40, "21" to 32, "22" to 22, "23" to 90, "24" to 28)
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
        // 1. 안내 섹션
        Text("내일은 이것을 확인해보세요!", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "오늘은 건조함과 우유 함유 식품이 함께 관찰됐어요.\n같은 조건에서 긁음이 반복되는지 확인해 주세요.",
            fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(49.dp))

        // 2. 시간대별 긁음 분석
        Text("시간대별 긁음 분석", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text("저녁 23시에서 24시 사이에\n집중적으로 발생했어요.", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        // 💡 [요청하신 25.dp 간격 적용]
        Spacer(modifier = Modifier.height(25.dp))

        // 3. 토글 버튼 (새벽, 오전, 오후, 저녁)
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

        // 4. 차트 영역
        val maxCount = (hourlyData.maxOfOrNull { it.second } ?: 100).coerceAtLeast(1)

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 막대 그래프 레이어
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

            // X축 시간 라벨
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

        // 5. 하단 요약 박스
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