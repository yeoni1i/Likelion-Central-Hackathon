package com.example.atocuemobile.ui.screen.timeline.scratch.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.screen.timeline.model.ScratchEvent

private fun hourLabelParts(hour: Int): Pair<String, String> {
    val period = if (hour < 12) "AM" else "PM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return displayHour.toString().padStart(2, '0') + ":00" to period
}

@Composable
fun ScratchHourSection(
    hour: Int,
    events: List<ScratchEvent>
) {
    val (timeText, period) = hourLabelParts(hour)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // 좌측 시간 표시 ("01:00 AM")
        Column(
            modifier = Modifier.width(56.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = timeText, fontSize = 12.sp, color = Color(0xFF8E8E93))
            Text(text = period, fontSize = 12.sp, color = Color(0xFF8E8E93))
        }

        // 우측 라인선 및 콘텐츠 영역
        Column(modifier = Modifier.weight(1f)) {
            // 시간 텍스트의 첫 줄 기준선에 라인 맞춤 (약 6dp 여백)
            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)

            if (events.isEmpty()) {
                // 1. 긁음 기록이 없을 때: 라인선 기준 위아래 37.5dp 여백
                Text(
                    text = "긁음 기록 없음",
                    fontSize = 13.sp,
                    color = Color(0xFF8E8E93),
                    fontWeight = FontWeight(400),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 37.5.dp) // ★ 긁음 기록 없음 위아래 37.5dp
                )
            } else {
                // 2. 기록 카드가 있을 때: 라인선과 카드 사이 14dp 간격
                Spacer(modifier = Modifier.height(14.dp)) // ★ 정각 라인선 ↔ 박스 거리 14dp

                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp) // ★ 카드와 카드 사이 거리 14dp
                ) {
                    events.forEach { event ->
                        ScratchEventItem(event)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp)) // ★ 마지막 카드 ↔ 다음 섹션 사이 하단 간격 14dp
            }
        }
    }
}