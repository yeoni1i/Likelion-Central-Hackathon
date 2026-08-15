package com.example.atocuemobile.ui.screen.timeline.component

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.screen.timeline.AtoCueBlue
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

private val dayLabels = listOf("일", "월", "화", "수", "목", "금", "토")

@Composable
fun WeekCalendar(
    month: String,
    selectedDate: Int,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelect: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Text(
            text = month,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        val today = LocalDate.now()
        val base = today.withDayOfMonth(
            selectedDate.coerceIn(1, today.lengthOfMonth())
        )
        val startOfWeek = base.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val weekDates = (0..6).map { startOfWeek.plusDays(it.toLong()) }

        Row(modifier = Modifier.fillMaxWidth()) {
            weekDates.forEachIndexed { index, date ->
                val isSelected = date.dayOfMonth == selectedDate
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) AtoCueBlue else Color.Transparent
                        )
                        .clickable { onDateSelect(date.dayOfMonth) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = dayLabels[index],
                            fontSize = 11.sp,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = date.dayOfMonth.toString(),
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}