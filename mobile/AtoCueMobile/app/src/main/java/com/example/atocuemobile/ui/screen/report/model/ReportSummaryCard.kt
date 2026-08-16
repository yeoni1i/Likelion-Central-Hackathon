package com.example.atocuemobile.ui.screen.report.model

// 1순위/2순위/3순위 카드 (사진2)
data class ReportSummaryCard(
    val rank: Int,                    // 1, 2, 3
    val title: String,                // "건조한 환경", "야외 체육 활동" 등
    val description: String,          // "어제 대비 낮아진 습도가..." 설명 문구
    val metricLabel: String,          // "오늘습도", "미세먼지" 등
    val metricValue: String,          // "35%", "매우 나쁨" 등
    val metricChangeLabel: String,    // "-8%", "3단계" 등
    val metricChangeIsBad: Boolean,   // true면 빨강(나쁨), false면 파랑 등
    val quote: String? = null         // 2순위 카드처럼 인용구가 있는 경우 (없으면 null)
)