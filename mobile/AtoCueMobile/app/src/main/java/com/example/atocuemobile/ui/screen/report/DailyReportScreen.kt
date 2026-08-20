package com.example.atocuemobile.ui.screen.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.atocuemobile.network.dto.HourlyScratchDto
import com.example.atocuemobile.network.dto.WeeklyTrendDto
import com.example.atocuemobile.ui.component.BottomNavTab
import com.example.atocuemobile.ui.component.BottomNavigationBar
import com.example.atocuemobile.ui.screen.report.component.DailyReportHeaderSection
import com.example.atocuemobile.ui.screen.report.component.HourlyScratchAnalysisSection
import com.example.atocuemobile.ui.screen.report.component.ReportDisclaimerFooter
import com.example.atocuemobile.ui.screen.report.component.WeeklyTrendAnalysisSection

@Composable
fun DailyReportScreen(
    userId: Long,
    selectedTab: BottomNavTab = BottomNavTab.REPORT,
    onTabSelected: (BottomNavTab) -> Unit = {},
    viewModel: ReportViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                ReportViewModel(userId = userId)
            }
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF6F7FB))
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "일간 리포트",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        },
        containerColor = Color(0xFFF5F6F8)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is ReportUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is ReportUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchDailyReport() }) {
                            Text("다시 시도")
                        }
                    }
                }

                is ReportUiState.Success -> {
                    val daily = state.report.dailyAnalysis
                    val weekly = state.report.weeklyAnalysis
                    val aiReport = state.report.aiReport

                    // AI summary 전체를 큰 제목에 넣지 않고,
                    // 실제 자극 요인 1~2순위로 짧은 헤드라인을 구성한다.
                    val factors = aiReport?.analysis?.triggerFactors.orEmpty()

                    val headerTitle = when {
                        factors.size >= 2 ->
                            "${factors[0].factor}와 ${factors[1].factor}가 관찰됐어요"

                        factors.size == 1 ->
                            "${factors[0].factor}이 주요 변화로 관찰됐어요"

                        daily.scratchCount > 0 ->
                            "오늘은 긁음 변화가 관찰됐어요"

                        else ->
                            "오늘은 비교적 안정적인 하루였어요"
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // 1. 헤더
                        DailyReportHeaderSection(
                            dateText = daily.date,

                            comparisonText =
                                if (daily.scratchCount > (weekly?.dailyAverage ?: 0.0)) {
                                    "평소보다 긁음이 많았어요"
                                } else {
                                    "평소와 비슷하거나 적게 긁었어요"
                                },

                            summaryTitle = headerTitle,

                            triggerFactors = factors,

                            onPrevDateClick = {
                                viewModel.goToPreviousDay()
                            },

                            onNextDateClick = {
                                viewModel.goToNextDay()
                            }
                        )

                        // 2. 시간대별 긁음 분석
                        HourlyScratchAnalysisSection(
                            hourlyScratch = daily.hourly.map { dto ->
                                HourlyScratchDto(
                                    hour = dto.hour,
                                    count = dto.count.toLong()
                                )
                            },

                            pattern =
                                aiReport?.analysis?.pattern
                                    ?: daily.peakHour?.let {
                                        "${it}시대에 가장 많이 긁었어요"
                                    }
                                    ?: "특이 패턴 없음",

                            carePoint =
                                aiReport?.analysis?.carePoint
                                    ?: "보습제를 주기적으로 덧발라주세요"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 3. 주간 추이 분석
                        if (weekly != null) {
                            WeeklyTrendAnalysisSection(
                                weeklyTrend = weekly.daily.map { dto ->
                                    WeeklyTrendDto(
                                        date = dto.date,
                                        count = dto.count.toInt()
                                    )
                                },
                                weeklyAverage = weekly.dailyAverage,
                                changePercent =
                                    if (weekly.dailyAverage > 0) {
                                        ((daily.scratchCount - weekly.dailyAverage) /
                                                weekly.dailyAverage) * 100
                                    } else {
                                        0.0
                                    }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 4. 하단 안내 문구
                        ReportDisclaimerFooter()
                    }
                }
            }
        }
    }
}