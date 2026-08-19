package com.example.atocuemobile.ui.screen.report

import androidx.compose.ui.graphics.Color

// 메인 브랜드 컬러 (선택된 날짜, 탭 밑줄, 버튼 등에 쓰이는 파랑)
val AtoCueBlue = Color(0xFF5398FF)
val AtoCueBlueLight = Color(0xFFEFF3FF)     // 연한 배경용
val AtoCueLightBlue = AtoCueBlueLight       // 👈 컴포넌트 호환용 추가!

// 긁음 감지 레벨별 색상 (첫번째 사진 참고: 안정/보통/주의/위험)
val LevelStable = Color(0xFF00CF90)
val LevelNormal = Color(0xFF76AFFF)
val LevelCaution = Color(0xFFFFC529)
val LevelDanger = Color(0xFFF8703E)
val LevelSevere = Color(0xFFEE4444)

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

val MainBackGroundColor = Color(0xFFFFFFFF)