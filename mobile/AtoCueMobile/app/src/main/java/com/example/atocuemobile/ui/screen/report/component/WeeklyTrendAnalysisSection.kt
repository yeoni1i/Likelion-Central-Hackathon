package com.example.atocuemobile.ui.screen.report.component

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.network.dto.WeeklyTrendDto
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


private val defaultWeeklyData = listOf(
    "일" to 30, "월" to 55, "화" to 42,
    "수" to 20, "목" to 85, "금" to 35, "토" to 48
)

@Composable
fun WeeklyTrendAnalysisSection(
    weeklyTrend: List<WeeklyTrendDto>,
    weeklyAverage: Double,
    changePercent: Double
) {
    val weeklyData = remember(weeklyTrend) {
        weeklyTrend.map {
            val date = LocalDate.parse(it.date)

            date.dayOfWeek.getDisplayName(
                TextStyle.SHORT,
                Locale.KOREAN
            ) to it.count
        }
    }

    val averageCount = weeklyAverage.toInt()

    val maxIndex = remember(weeklyData) {
        weeklyData.indices.maxByOrNull { weeklyData[it].second } ?: 0
    }

    var selectedBarIndex by remember(weeklyData) {
        mutableStateOf(maxIndex)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Text("주간 추이", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text("최근 7일보다\n증상이 심해졌어요", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(30.dp))

        val maxCount = (weeklyData.maxOfOrNull { it.second } ?: 100).coerceAtLeast(1)
        val avgYFraction = 1f - (averageCount.toFloat() / maxCount.toFloat() * 0.7f)

        val barWidth = 22.dp
        val barSpacing = 12.dp
        val rightPadding = 19.dp

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                // 1. 막대 그래프 레이어 (아래 배치)
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = rightPadding),
                    horizontalArrangement = Arrangement.spacedBy(barSpacing, Alignment.End),
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyData.forEachIndexed { index, (day, count) ->
                        val isSelected = index == selectedBarIndex
                        val barHeightFraction = (count.toFloat() / maxCount.toFloat()).coerceIn(0.1f, 1.0f)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier
                                .width(barWidth)
                                .fillMaxHeight()
                                .clickable { selectedBarIndex = index }
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .wrapContentWidth(unbounded = true)
                                        .height(28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AtoCueBlue)
                                        .padding(horizontal = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "총 ${count}회",
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            } else {
                                Spacer(modifier = Modifier.height(34.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .width(barWidth)
                                    .fillMaxHeight(barHeightFraction * 0.7f)
                                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                                    .background(if (isSelected) AtoCueBlue else Color(0xFFE5E5E5))
                            )
                        }
                    }
                }

                // 2. 평균선 Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    val yPos = size.height * avgYFraction
                    drawLine(
                        color = Color(0xFFEE4444),
                        start = Offset(0f, yPos),
                        end = Offset(size.width, yPos),
                        strokeWidth = 2.dp.toPx()
                    )
                }

                // 3. 평균선 텍스트
                Text(
                    text = "평균 ${averageCount}회",
                    color = Color(0xFFEE4444),
                    fontSize = 12.sp,
                    fontWeight = FontWeight(600),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = (150 * avgYFraction + 4).dp)
                )
            }

            // 4. 전체 수평 X축선
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color(0xFFE5E5E5)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 5. X축 요일 라벨
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = rightPadding),
                horizontalArrangement = Arrangement.spacedBy(barSpacing, Alignment.End)
            ) {
                weeklyData.forEach { (day, _) ->
                    Box(
                        modifier = Modifier.width(barWidth),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(day, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 하단 요약 박스 (텍스트는 아직 고정 — /analysis/daily 붙기 전까지 유지)
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
                            append("최근 7일 중 오늘 긁음이 ")
                        }
                        withStyle(style = SpanStyle(color = Color(0xFF000000), fontWeight = FontWeight(400), fontSize = 13.sp)) {
                            append("가장 많았어요")
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
                            append("최근 일주일 평균보다 ")
                        }
                        withStyle(style = SpanStyle(color = Color(0xFF000000), fontWeight = FontWeight(400), fontSize = 13.sp)) {
                            append(
                                if (changePercent >= 0) {
                                    "${"%.1f".format(changePercent)}% 증가했어요"
                                } else {
                                    "${"%.1f".format(-changePercent)}% 감소했어요"
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}