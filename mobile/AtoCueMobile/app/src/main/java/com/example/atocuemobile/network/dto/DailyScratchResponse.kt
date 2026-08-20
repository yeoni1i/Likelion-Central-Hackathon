package com.example.atocuemobile.network.dto

// ===== 기존 =====
data class DailyScratchResponse(
    val date: String,
    val eventCount: Int,
    val totalSeconds: Double,
    val averageIntensity: Double,
    val wearHours: Double,
    val scratchSecondsPerWearHour: Double
)

// ===== 2. 주간 추이 (/analysis/reports/weekly) =====
data class WeeklyScratchResponse(
    val weekStartDate: String,
    val weekEndDate: String,
    val averageCount: Double,
    val dailyCounts: List<DailyCountDto>
)

data class DailyCountDto(
    val date: String,        // "2026-08-20"
    val dayLabel: String,     // "일","월",... 백엔드가 요일 문자열로 내려주면 편함
    val eventCount: Int
)

// ===== 3. AI 원인 분석 리포트 (/analysis/daily) — scratch_cause_analyses 기반 제안 =====
data class DailyAiReportResponse(
    val analysisDate: String,
    val summaryTitle: String,       // 헤더 "유제품과 건조한 환경이 의심돼요"
    val changeRateText: String?,    // "어제보다 긁음 18%↑" — null이면 헤더에서 숨김 처리
    val causes: List<CauseAnalysisDto>   // 1~3순위 카드
)

data class CauseAnalysisDto(
    val rank: Int,
    val category: String,               // "건조한 환경","야외 체육 활동","유제품 간식"
    val description: String,
    val metrics: List<CauseMetricDto> = emptyList(),      // 카드1 타입 (습도/미세먼지)
    val relatedLogs: List<RelatedLogDto> = emptyList()    // 카드3 타입 (간식 이력)
)

data class CauseMetricDto(
    val label: String,       // "오늘습도"
    val value: String,       // "35%"
    val changeText: String,  // "-8%"
    val isGoodTrend: Boolean // true=파란배지, false=빨간배지
)

data class RelatedLogDto(
    val tag: String,   // "간식"
    val date: String,  // "오늘","7.3"
    val name: String   // "크림빵"
)

// ===== 4. 위험 식단 리스트 (신규 제안: /analysis/reports/risk-foods) — daily_log_foods 기반 =====
data class RiskFoodListResponse(
    val baseDate: String,
    val items: List<RiskFoodItemDto>
)

data class RiskFoodItemDto(
    val foodName: String,
    val reactionCount: Int
)

data class ScratchEventDto(
    val id: Long?,
    val startTime: String?,
    val durationSeconds: Double?,
    val intensity: Double?
)
