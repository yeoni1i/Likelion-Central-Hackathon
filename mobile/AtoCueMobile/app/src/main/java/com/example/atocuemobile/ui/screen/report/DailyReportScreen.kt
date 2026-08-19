package com.example.atocuemobile.ui.screen.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.screen.report.component.*

@Composable
fun DailyReportScreen() {
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
        containerColor = Color(0xFFF5F6F8) // 첫 번째 카드 영역 아래에서 구분되는 바탕 배경색
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // 1. 헤더 (카드 밑으로 갭 36dp)
            DailyReportHeaderSection()

            // 2. 시간대별 긁음 분석 (배경 흰색)
            HourlyScratchAnalysisSection()

            Spacer(modifier = Modifier.height(12.dp))

            // 3. 주간 추이 분석
            WeeklyTrendAnalysisSection()

            Spacer(modifier = Modifier.height(12.dp))

            // 4. 위험 식단 리스트
            RiskFoodListSection()

            Spacer(modifier = Modifier.height(12.dp))

            // 5. 하단 안내 문구
            ReportDisclaimerFooter()
        }
    }
}