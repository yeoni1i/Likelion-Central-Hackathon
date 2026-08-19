package com.example.atocuemobile.ui.screen.report.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AtoCueBlue = Color(0xFF3B82F6)

private val tightTextStyle = TextStyle(
    platformStyle = PlatformTextStyle(
        includeFontPadding = false
    )
)

@Composable
fun DailyReportHeaderSection(
    dateText: String = "0월 00일 (요일), 오늘",
    summaryTitle: String = "유제품과 건조한 환경이 의심돼요",
    subSummaryText: String = "어제보다 긁음 18%↑",
    onPrevDateClick: () -> Unit = {},
    onNextDateClick: () -> Unit = {}
) {
    val headerGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF6F7FB),
            Color(0xFFE0EAFF),
            Color(0xFFF6F7FB)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = headerGradient)
            .padding(top = 16.dp)
    ) {
        // 1. 날짜 선택 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevDateClick) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "이전 날짜")
            }
            Text(
                text = dateText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                style = tightTextStyle,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            IconButton(onClick = onNextDateClick) {
                Icon(Icons.Default.ChevronRight, contentDescription = "다음 날짜")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. 메인 요약 영역
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "평소보다 긁음이 많았어요",
                fontSize = 15.sp,
                color = Color.DarkGray,
                style = tightTextStyle
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = summaryTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                style = tightTextStyle
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subSummaryText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFF5252),
                style = tightTextStyle
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // 3. 카드 가로 스크롤 영역
        LazyRow(
            contentPadding = PaddingValues(start = 60.dp, end = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(17.dp)
        ) {
            // [카드 1] 건조한 환경
            item {
                Box(
                    modifier = Modifier
                        .size(width = 240.dp, height = 275.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopStart)
                            .padding(top = 22.dp, start = 22.dp, end = 22.dp)
                    ) {
                        Text(
                            text = "1순위",
                            fontSize = 13.sp,
                            color = AtoCueBlue,
                            fontWeight = FontWeight.Bold,
                            style = tightTextStyle
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = "건조한 환경",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            style = tightTextStyle
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "어제 대비 낮아진 습도가 오늘의\n긁음 증가의 가장 큰 요인으로 의심돼요",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            lineHeight = 15.sp,
                            style = tightTextStyle
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(start = 22.dp, end = 22.dp, bottom = 26.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("오늘습도", fontSize = 10.sp, color = Color.Gray, style = tightTextStyle)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("35%", fontSize = 20.sp, fontWeight = FontWeight.Normal, style = tightTextStyle)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFEBF3FF))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .size(width =53.dp, height = 24.dp),
                                    contentAlignment = Alignment.Center
                            ) {
                                Text("▼ -8%", fontSize = 14.sp, color = AtoCueBlue, fontWeight = FontWeight.Bold, style = tightTextStyle)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("미세먼지", fontSize = 10.sp, color = Color.Gray, style = tightTextStyle)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("매우 나쁨", fontSize = 18.sp, fontWeight = FontWeight.Normal, style = tightTextStyle)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFFEBEE))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .size(width =53.dp, height = 24.dp),
                                    contentAlignment = Alignment.Center
                            ) {
                                Text("▲ 3단계", fontSize = 13.sp, color = Color(0xFFFF5252), fontWeight = FontWeight.Bold, style = tightTextStyle)
                            }
                        }
                    }
                }
            }

            // [카드 2] 야외 체육 활동
            item {
                Box(
                    modifier = Modifier
                        .size(width = 240.dp, height = 275.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopStart)
                            .padding(top = 22.dp, start = 22.dp, end = 22.dp)
                    ) {
                        Text("2순위", fontSize = 13.sp, color = AtoCueBlue, fontWeight = FontWeight.Bold, style = tightTextStyle)
                        Spacer(modifier = Modifier.height(5.dp))
                        Text("야외 체육 활동", fontSize = 16.sp, fontWeight = FontWeight.Bold, style = tightTextStyle)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "격한 야외 활동 후 땀띠와 건조한 환경이\n겹쳐 증상이 악화된 걸로 보여요.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            lineHeight = 15.sp,
                            style = tightTextStyle
                        )
                    }

                    // 💡 [수정] 두 번째 카드 하단 하늘색 박스 크기 (196dp x 98dp)
                    Box(
                        modifier = Modifier
                            .size(width = 196.dp, height = 98.dp)
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 28.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEBF3FF))
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "\" 학교에서 야외 체육 활동 후\n땀띠 증상이 발견되었음 \"",
                                fontSize = 11.sp,
                                color = AtoCueBlue,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp,
                                style = tightTextStyle
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("오늘 | 특이사항 기록", fontSize = 10.sp, color = Color.Gray, style = tightTextStyle)
                        }
                    }
                }
            }

            // [카드 3] 유제품 간식
            item {
                Box(
                    modifier = Modifier
                        .size(width = 240.dp, height = 275.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopStart)
                            .padding(top = 22.dp, start = 22.dp, end = 22.dp)
                    ) {
                        Text("3순위", fontSize = 13.sp, color = AtoCueBlue, fontWeight = FontWeight.Bold, style = tightTextStyle)
                        Spacer(modifier = Modifier.height(5.dp))
                        Text("유제품 간식", fontSize = 16.sp, fontWeight = FontWeight.Bold, style = tightTextStyle)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "간식섭취 후 긁음이 증가했어요\n이전에도 유사한 증상이 기록되었어요",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            lineHeight = 15.sp,
                            style = tightTextStyle
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(start = 22.dp, end = 22.dp, bottom = 38.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(
                            Triple("간식", "오늘", "크림빵"),
                            Triple("간식", "7.3", "소프트 콘"),
                            Triple("간식", "6.3", "블루베리 요거트 음료")
                        ).forEach { (tag, date, name) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AtoCueBlue)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(tag, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Medium, style = tightTextStyle)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(date, fontSize = 11.sp, color = Color.Gray, style = tightTextStyle)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.Black, style = tightTextStyle)
                            }
                        }
                    }
                }
            }
        }

        // 스크롤 카드 하단 여백 36dp
        Spacer(modifier = Modifier.height(36.dp))

        // 4. 다음 섹션과 이어지는 상단 모서리 25dp 흰색 곡선 배경 커넥터
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(25.dp)
                .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
                .background(Color.White)
        )
    }
}