package com.example.atocuemobile.ui.screen.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.atocuemobile.ui.screen.report.component.ReportDateSelector

@Composable
fun ReportScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // TODO: 날짜 상태 관리 (선택된 날짜, onDateChange 콜백)
        ReportDateSelector(
            dateLabel = "0월 00일 (요일), 오늘",
            onPrevDay = { /* TODO */ },
            onNextDay = { /* TODO */ }
        )

        // TODO: 값은 실제 데이터 연동 후 채우기
        ReportSummarySection(
            title = "",       // 예: "탄소보다 곰팡이 있었어요"
            cause = "",        // 예: "건조한 환경이 의심돼요"
            changeRate = ""    // 예: "어제보다 곰팡 18%↑"
        )

        HourlyPatternChart(
            data = emptyList()  // TODO: 시간대별 데이터 리스트
        )

        SymptomTrendChart(
            data = emptyList()  // TODO: 최근 며칠 증상 데이터 리스트
        )

        RecommendedProductList(
            products = emptyList()  // TODO: 추천 제품 리스트
        )
    }
}