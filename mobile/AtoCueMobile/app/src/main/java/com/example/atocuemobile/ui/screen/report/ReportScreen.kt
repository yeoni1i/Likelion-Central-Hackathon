package com.example.atocuemobile.ui.screen.report

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.atocuemobile.ui.screen.report.component.HourlyAnalysisSection
import com.example.atocuemobile.ui.screen.report.component.ReportDateHeader
import com.example.atocuemobile.ui.screen.report.component.RiskFoodListSection
import com.example.atocuemobile.ui.screen.report.component.SummaryCardRow
import com.example.atocuemobile.ui.screen.report.component.WeeklyTrendSection
import com.example.atocuemobile.ui.screen.report.model.*
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun ReportScreen() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTimeOfDay by remember { mutableStateOf(TimeOfDay.EVENING) }

    // TODO: 아래 더미 데이터 전부 ViewModel에서 selectedDate 기준으로 실제 데이터 불러오도록 교체
    val summaryCards = remember(selectedDate) {
        listOf(
            ReportSummaryCard(
                rank = 1,
                title = "건조한 환경",
                description = "어제 대비 낮아진 습도가 오늘의 긁음 증가의 가장 큰 요인으로 의심돼요",
                metricLabel = "오늘습도",
                metricValue = "35%",
                metricChangeLabel = "-8%",
                metricChangeIsBad = true
            ),
            ReportSummaryCard(
                rank = 2,
                title = "야외 체육 활동",
                description = "격한 야외 활동 후 땀과 건조한 환경이 겹쳐 증상이 악화된 걸로 보여요.",
                metricLabel = "",
                metricValue = "",
                metricChangeLabel = "",
                metricChangeIsBad = false,
                quote = "학교에서 야외 체육 활동 후 땀띠 증상이 발견되었음"
            ),
            ReportSummaryCard(
                rank = 3,
                title = "유제품 간식",
                description = "간식섭취 후 긁음이 증가했어요\n이전에도 유사한 증상이 기록되었어요",
                metricLabel = "",
                metricValue = "",
                metricChangeLabel = "",
                metricChangeIsBad = false
            )
        )
    }

    val hourlyData = remember(selectedDate) {
        (1..24).map { hour -> HourlyScratchData(hour, if (hour == 23) 90 else (0..20).random()) }
    }

    val weeklyData = remember(selectedDate) {
        listOf(
            WeeklyTrendData(DayOfWeek.SUNDAY, 30),
            WeeklyTrendData(DayOfWeek.MONDAY, 55),
            WeeklyTrendData(DayOfWeek.TUESDAY, 45),
            WeeklyTrendData(DayOfWeek.WEDNESDAY, 20),
            WeeklyTrendData(DayOfWeek.THURSDAY, 85),
            WeeklyTrendData(DayOfWeek.FRIDAY, 40),
            WeeklyTrendData(DayOfWeek.SATURDAY, 50)
        )
    }

    val riskFoods = remember(selectedDate) {
        listOf(
            RiskFoodItem(5, "유제품, 밀가루"),
            RiskFoodItem(4, "00식품"),
            RiskFoodItem(3, "00식품"),
            RiskFoodItem(2, "00식품")
            // TODO: 5개 이상 있으면 페이지네이션 자동으로 동작함 (RiskFoodListSection이 처리)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)
    ) {
        ReportDateHeader(
            selectedDate = selectedDate,
            onPrevDay = { selectedDate = selectedDate.minusDays(1) },
            onNextDay = { selectedDate = selectedDate.plusDays(1) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SummaryCardRow(cards = summaryCards)

        Spacer(modifier = Modifier.height(28.dp))

        HourlyAnalysisSection(
            hourlyData = hourlyData,
            peakHour = hourlyData.maxByOrNull { it.count }?.hour,
            selectedTimeOfDay = selectedTimeOfDay,
            onTimeOfDaySelect = { selectedTimeOfDay = it }
        )

        Spacer(modifier = Modifier.height(28.dp))

        WeeklyTrendSection(
            weeklyData = weeklyData,
            averageCount = weeklyData.map { it.count }.average().toInt()
        )

        Spacer(modifier = Modifier.height(28.dp))

        RiskFoodListSection(items = riskFoods, baseDate = selectedDate)

        Spacer(modifier = Modifier.height(40.dp))
    }
}