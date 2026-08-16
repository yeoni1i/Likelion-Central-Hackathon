package com.example.atocuemobile.ui.screen.record.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
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

@Composable
fun RecordDatePickerDialog(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelect: (LocalDate) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "날짜입력")
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "닫기")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${yearMonth.year}년 ${yearMonth.monthValue}월")
                        Row {
                            IconButton(onClick = onPrevMonth) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "이전 달")
                            }
                            IconButton(onClick = onNextMonth) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "다음 달")
                            }
                        }
                    }

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
                    val prevMonth = yearMonth.minusMonths(1)
                    val prevMonthDays = prevMonth.lengthOfMonth()

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.height(280.dp)
                    ) {
                        items(startOffset) { index ->
                            val day = prevMonthDays - startOffset + index + 1
                            Box(modifier = Modifier.padding(4.dp).size(40.dp), contentAlignment = Alignment.Center) {
                                Text(text = day.toString(), color = Color.LightGray)
                            }
                        }
                        items((1..daysInMonth).toList()) { day ->
                            val date = yearMonth.atDay(day)
                            val isSelected = date == selectedDate
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp)) // TODO: 원하는 모양(원/사각형)으로 조절
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { onDateSelect(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = day.toString(), color = if (isSelected) Color.White else Color.Black)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text(text = "확인")
                    }
                }
            }
        }
    }
}