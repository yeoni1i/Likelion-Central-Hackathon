package com.example.atocuemobile.ui.screen.timeline.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.runtime.remember
import com.example.atocuemobile.ui.screen.timeline.AtoCueBlue

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
        // usePlatformDefaultWidth = false 해야 화면 꽉 채운 커스텀 배치 가능
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)) // 뒤 화면 어둡게
                .clickable(
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                ) { onDismiss() } // 어두운 배경 탭하면 닫힘
        ) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                    ) { /* 카드 내부 클릭은 무시(닫히지 않게) */ },
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 16.dp)
                    // TODO: top padding 48dp는 상태바 높이 임시값.
                    // 실제로는 windowInsets(statusBars)로 정확히 맞추는 게 안전함
                ) {
                    Text(text = "타임라인")
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${yearMonth.monthValue}월")
                        Row {
                            IconButton(onClick = onPrevMonth) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "이전 달")
                            }
                            IconButton(onClick = onNextMonth) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "다음 달")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("일", "월", "화", "수", "목", "금", "토").forEach {
                            Text(
                                text = it,
                                modifier = Modifier.weight(1f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    val firstDayOfMonth = yearMonth.atDay(1)
                    val daysInMonth = yearMonth.lengthOfMonth()
                    val startOffset = firstDayOfMonth.dayOfWeek.value % 7

                    // 이전 달 마지막 날짜들 (흐리게 표시, 피그마상 28~30 같은 부분)
                    val prevMonth = yearMonth.minusMonths(1)
                    val prevMonthDays = prevMonth.lengthOfMonth()

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.height(280.dp)
                    ) {
                        items(startOffset) { index ->
                            val day = prevMonthDays - startOffset + index + 1
                            Box(
                                modifier = Modifier.padding(4.dp).size(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = day.toString(), color = Color.Gray)
                            }
                        }
                        items((1..daysInMonth).toList()) { day ->
                            val date = yearMonth.atDay(day)
                            val isSelected = date == selectedDate
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(40.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                    .background(
                                        if (isSelected) AtoCueBlue
                                        else Color.Transparent
                                    )
                                    .clickable { onDateSelect(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = day.toString())
                                // TODO: isSelected일 때 글자색 흰색으로
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 하단 드래그 핸들 바 (피그마 스샷 맨 아래 회색 바)
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.LightGray)
                    )
                }
            }
        }
    }
}