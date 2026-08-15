package com.example.atocuemobile.ui.screen.timeline

import androidx.compose.ui.graphics.Color

// 메인 브랜드 컬러 (선택된 날짜, 탭 밑줄, 버튼 등에 쓰이는 파랑)
val AtoCueBlue = Color(0xFF5398FF)          // TODO: 피그마 정확한 hex로 교체
val AtoCueBlueLight = Color(0xFFEFF3FF)     // 연한 배경용

// 긁음 감지 레벨별 색상 (첫번째 사진 참고: 안정/보통/주의/위험)
val LevelStable = Color(0xFF00CF90)   // 초록 - 안정
val LevelNormal = Color(0xFF76AFFF)   // 파랑 - 보통
val LevelCaution = Color(0xFFFFC529)  // 노랑 - 주의
val LevelDanger = Color(0xFFF8703E)   // 빨강 - 위험
// TODO: 각 색상 정확한 hex는 피그마 Dev Mode에서 확인 후 교체

// 텍스트 색상
val TextPrimary = Color(0xFF000000)
val TextSecondary = Color(0xFFADAFB2)   // 회색 보조 텍스트 (발생시각, 안내문구 등)

// 배경/카드 색상
val BackgroundGray = Color(0xFFFAFAFA)  // 카드 배경 (비어있는 식단 박스, 증상 칩 배경 등)
val BorderGray = Color(0xFFEBEBEB)

val CardBackground = Color(0xFFF5F5F7)   // 아코디언 카드 전체 배경
val ChipBackground = Color(0xFFFFFFFF)   // 버튼/증상칩 배경 (흰색)
val ChipBorder = Color(0xFFEBEBEB)       // 버튼 테두리
val SliderTrackGray = Color(0xFFE5E7EC)  // 슬라이더 트랙 회색