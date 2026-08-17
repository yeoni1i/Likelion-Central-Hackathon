package com.example.atocuemobile.ui.screen.timeline.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.atocuemobile.ui.screen.timeline.AtoCueBlue
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun MonthCalendarDialog(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelect: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() }
        ) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { /* 카드 내부 클릭은 무시 */ },
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = "타임라인",
                        fontSize = 20.sp,
                        fontWeight = FontWeight(600)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${yearMonth.monthValue}월",
                            fontSize = 18.sp,
                            fontWeight = FontWeight(500)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 🌟 3. 화살표 아이콘 사이즈 확대 (12dp -> 24dp) 및 터치 영역 조절
                            IconButton(
                                onClick = onPrevMonth,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "이전 달",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Black
                                )
                            }
                            IconButton(
                                onClick = onNextMonth,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "다음 달",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("일", "월", "화", "수", "목", "금", "토").forEach {
                            Text(
                                text = it,
                                fontSize = 14.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFF6C6E72),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val firstDayOfMonth = yearMonth.atDay(1)
                    val daysInMonth = yearMonth.lengthOfMonth()
                    val startOffset = firstDayOfMonth.dayOfWeek.value % 7

                    val prevMonth = yearMonth.minusMonths(1)
                    val prevMonthDays = prevMonth.lengthOfMonth()

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // 이전 달 날짜
                        items(startOffset) { index ->
                            val day = prevMonthDays - startOffset + index + 1
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    fontSize = 15.sp,
                                    color = Color(0xFFD0D6DD)
                                )
                            }
                        }

                        // 이번 달 날짜
                        items((1..daysInMonth).toList()) { day ->
                            val date = yearMonth.atDay(day)
                            val isSelected = date == selectedDate

                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp) // 🌟 2. 완벽한 정사각형 (36dp x 36dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) AtoCueBlue
                                            else Color.Transparent
                                        )
                                        .clickable { onDateSelect(date) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        fontSize = 15.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 하단 드래그 핸들 바
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFD9D9D9))
                    )
                }
            }
        }
    }
}