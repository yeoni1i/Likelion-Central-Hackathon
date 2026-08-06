package com.example.atocuemobile.ui.screen.timeline

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.atocuemobile.ui.screen.timeline.component.WeekCalendar

private val tabTitles = listOf("긁음 감지", "식단기록", "생활기록")

@Composable
fun TimelineScreen(
    onAddRecordClick: () -> Unit  // "새로운 기록 추가" 눌렀을 때 record 화면으로 이동
) {
    var selectedTab by remember { mutableIntStateOf(1) } // 디자인상 기본은 "식단기록"

    Column(modifier = Modifier.fillMaxSize()) {
        // TODO: 선택된 날짜 상태 관리 (WeekCalendar와 연결)
        WeekCalendar(
            month = "7월",  // TODO: 실제 월 값으로 교체
            selectedDate = 24,  // TODO: 실제 선택된 날짜로 교체
            onPrevMonth = { /* TODO */ },
            onNextMonth = { /* TODO */ },
            onDateSelect = { /* TODO */ }
        )

        TabRow(selectedTabIndex = selectedTab) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> ScratchDetectTab()
            1 -> MealRecordTab(onAddRecordClick = onAddRecordClick)
            2 -> LifeRecordTab()
        }
    }
}