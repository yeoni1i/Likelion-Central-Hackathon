package com.example.atocuemobile.ui.model

import androidx.compose.ui.graphics.Color
import com.example.atocuemobile.R

enum class ScratchStatus(
    val heroTitle: String,
    val badgeLabel: String,
    val badgeBgColor: Color,
    val badgeTextColor: Color,
    val cloudImageRes: Int
) {
    // 1단계: 안정
    STABLE(
        heroTitle = "감지된 긁음 없음",
        badgeLabel = "안정",
        badgeBgColor = Color(0x2600CF90), // rgba(0, 207, 144, 0.15)
        badgeTextColor = Color(0xFF01B67F),
        cloudImageRes = R.drawable.one
    ),

    // 2단계: 보통
    NORMAL(
        heroTitle = "주의가 필요해요",
        badgeLabel = "보통",
        badgeBgColor = Color(0x265398FF), // rgba(83, 152, 255, 0.15)
        badgeTextColor = Color(0xFF5398FF),
        cloudImageRes = R.drawable.two
    ),

    // 3단계: 경고
    WARNING(
        heroTitle = "긁음이 지속되고 있어요",
        badgeLabel = "경고",
        badgeBgColor = Color(0x33FFC529), // #FFC52933
        badgeTextColor = Color(0xFFFFBC00),
        cloudImageRes = R.drawable.three
    ),

    // 4단계: 위험
    DANGER(
        heroTitle = "심한 긁음이 감지되었어요",
        badgeLabel = "위험",
        badgeBgColor = Color(0x26FF6831), // #FF683126
        badgeTextColor = Color(0xFFF8703E),
        cloudImageRes = R.drawable.four
    ),

    // 5단계: 매우위험
    VERY_DANGER(
        heroTitle = "즉각적인 조치가 필요해요",
        badgeLabel = "매우위험",
        badgeBgColor = Color(0x33EE4444), // #EE444433
        badgeTextColor = Color(0xFFEE4444),
        cloudImageRes = R.drawable.five
    );

    companion object {
        // Int, String, Double, Any 모두 안전하게 변환
        fun fromIntensity(intensity: Any?): ScratchStatus {
            val str = intensity?.toString()?.trim() ?: return STABLE
            val level = str.toDoubleOrNull()?.toInt() ?: when (str.uppercase()) {
                "STABLE", "안정", "1" -> 1
                "NORMAL", "보통", "2" -> 2
                "WARNING", "경고", "주의", "3" -> 3
                "DANGER", "위험", "4" -> 4
                "VERY_DANGER", "매우위험", "5" -> 5
                else -> 1
            }

            return when (level) {
                1 -> STABLE
                2 -> NORMAL
                3 -> WARNING
                4 -> DANGER
                5 -> VERY_DANGER
                else -> if (level >= 5) VERY_DANGER else STABLE
            }
        }

        fun fromIntensity(intensity: Int): ScratchStatus {
            return fromIntensity(intensity as Any?)
        }

        fun fromIntensity(intensity: String?): ScratchStatus {
            return fromIntensity(intensity as Any?)
        }
    }
}