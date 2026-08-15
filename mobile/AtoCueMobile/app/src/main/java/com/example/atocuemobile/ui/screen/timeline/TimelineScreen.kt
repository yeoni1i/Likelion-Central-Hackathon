package com.example.atocuemobile.ui.screen.timeline

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.atocuemobile.ui.screen.timeline.component.MonthCalendarDialog
import com.example.atocuemobile.ui.screen.timeline.component.TimelineTopBar
import com.example.atocuemobile.ui.screen.timeline.component.WeekCalendar
import com.example.atocuemobile.ui.screen.timeline.life.LifeRecordTab
import com.example.atocuemobile.ui.screen.timeline.meal.MealRecordTab
import com.example.atocuemobile.ui.screen.timeline.scratch.ScratchDetectTab
import java.time.LocalDate
import java.time.YearMonth


private val tabTitles = listOf("긁음 감지", "식단기록", "생활기록")

@Composable
fun TimelineScreen(
    onAddRecordClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(1) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var displayedMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    var showCalendarDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TimelineTopBar(onCalendarClick = { showCalendarDialog = true })

        WeekCalendar(
            month = "${displayedMonth.monthValue}월",
            selectedDate = selectedDate.dayOfMonth,
            onPrevMonth = { displayedMonth = displayedMonth.minusMonths(1) },
            onNextMonth = { displayedMonth = displayedMonth.plusMonths(1) },
            onDateSelect = { day -> selectedDate = displayedMonth.atDay(day) }
        )

        TabRow(
            selectedTabIndex = selectedTab
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> ScratchDetectTab(date = selectedDate)
            1 -> MealRecordTab(date = selectedDate, onAddRecordClick = onAddRecordClick)
            2 -> LifeRecordTab(date = selectedDate)
        }
    }

    if (showCalendarDialog) {
        MonthCalendarDialog(
            yearMonth = displayedMonth,
            selectedDate = selectedDate,
            onPrevMonth = { displayedMonth = displayedMonth.minusMonths(1) },
            onNextMonth = { displayedMonth = displayedMonth.plusMonths(1) },
            onDateSelect = { date ->
                selectedDate = date
                displayedMonth = YearMonth.from(date)
                showCalendarDialog = false
            },
            onDismiss = { showCalendarDialog = false }
        )
    }
}