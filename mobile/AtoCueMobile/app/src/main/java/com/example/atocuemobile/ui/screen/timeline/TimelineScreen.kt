package com.example.atocuemobile.ui.screen.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.atocuemobile.ui.screen.timeline.component.MonthCalendarDialog
import com.example.atocuemobile.ui.screen.timeline.component.TimelineTopBar
import com.example.atocuemobile.ui.screen.timeline.component.WeekCalendar
import com.example.atocuemobile.ui.screen.timeline.life.LifeRecordTab
import com.example.atocuemobile.ui.screen.timeline.meal.MealRecordTab
import com.example.atocuemobile.ui.screen.timeline.scratch.ScratchDetectTab
import com.example.atocuemobile.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset


private val tabTitles = listOf("긁음 감지", "식단기록", "생활기록")

@Composable
fun TimelineScreen(
    homeViewModel: HomeViewModel,
    onAddRecordClick: () -> Unit,
    onNavigateToLifeRecordInput: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(1) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var displayedMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    var showCalendarDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackGroundColor)
    ) {
        TimelineTopBar(onCalendarClick = { showCalendarDialog = true })

        WeekCalendar(
            month = "${displayedMonth.monthValue}월",
            selectedDate = selectedDate.dayOfMonth,
            onPrevMonth = { displayedMonth = displayedMonth.minusMonths(1) },
            onNextMonth = { displayedMonth = displayedMonth.plusMonths(1) },
            onDateSelect = { day -> selectedDate = displayedMonth.atDay(day) }
        )

        // 1. 회색 넓은 구분선
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp), // 원하는 패딩 값 지정
            thickness = 8.dp,
            color = Color(0xFFF2F4F6)
        )

        // 2. 왼쪽 정렬 탭 (디프리케이트 부분 제거)
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = Color.White,
            contentColor = Color.Black, // 선택된 탭의 기본 인디케이터/텍스트 색상
            divider = {}, // 하단 전체에
        // 생기
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color.Black
                    )
                }
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.Gray
                )
            }
        }

        when (selectedTab) {
            0 -> ScratchDetectTab(
                date = selectedDate,
                homeViewModel = homeViewModel
            )

            1 -> MealRecordTab(
                date = selectedDate,
                onAddRecordClick = onAddRecordClick
            )

            2 -> LifeRecordTab(
                date = selectedDate,
                onNavigateToLifeRecordInput = onNavigateToLifeRecordInput
            )
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