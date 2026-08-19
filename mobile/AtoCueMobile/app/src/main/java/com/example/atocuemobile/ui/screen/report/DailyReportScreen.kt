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

// 💡 5개 컴포넌트 명시적 import
import com.example.atocuemobile.ui.screen.report.component.DailyReportHeaderSection
import com.example.atocuemobile.ui.screen.report.component.HourlyScratchAnalysisSection
import com.example.atocuemobile.ui.screen.report.component.WeeklyTrendAnalysisSection
import com.example.atocuemobile.ui.screen.report.component.RiskFoodListSection
import com.example.atocuemobile.ui.screen.report.component.RiskFoodItem
import com.example.atocuemobile.ui.screen.report.component.ReportDisclaimerFooter

@Composable
fun DailyReportScreen(
    viewModel: ReportViewModel = viewModel()
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
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchDailyReport() }) {
                            Text("다시 시도")
                        }
                    }
                }

                is ReportUiState.Success -> {
                    val bundle = state.report // ReportBundle(daily, weekly, aiReport, riskFoods)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // 1. 헤더 — daily 데이터만 실제 연결 (AI 원인문구는 아직 TODO)
                        DailyReportHeaderSection(
                            dateText = bundle.daily.date,
//                            eventCount = bundle.daily.eventCount
                            // summaryTitle, subSummaryText: HeaderSection 파라미터 추가 후 연결 예정
                        )

                        // 2. 시간대별 긁음 분석 — TODO: 대응 API 없음, 더미 유지
                        HourlyScratchAnalysisSection()

                        Spacer(modifier = Modifier.height(12.dp))

                        // 3. 주간 추이 분석 — TODO: WeeklyTrendAnalysisSection 파라미터 추가 후 bundle.weekly 연결
                        WeeklyTrendAnalysisSection()

                        Spacer(modifier = Modifier.height(12.dp))

                        // 4. 위험 식단 리스트 — 실제 연결 (riskFoods 없으면 기본 더미 리스트로 폴백)
                        RiskFoodListSection(
                            baseDateText = bundle.riskFoods?.baseDate?.let { "$it 기준" }
                                ?: "0월 00일 기준",
                            foodList = bundle.riskFoods?.items?.map {
                                RiskFoodItem(it.reactionCount, it.foodName)
                            } ?: listOf(
                                RiskFoodItem(5, "유제품, 밀가루"),
                                RiskFoodItem(4, "00식품"),
                                RiskFoodItem(3, "00식품"),
                                RiskFoodItem(2, "00식품"),
                                RiskFoodItem(1, "00식품")
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 5. 하단 안내 문구
                        ReportDisclaimerFooter()
                    }
                }
            }
        }
    }
}