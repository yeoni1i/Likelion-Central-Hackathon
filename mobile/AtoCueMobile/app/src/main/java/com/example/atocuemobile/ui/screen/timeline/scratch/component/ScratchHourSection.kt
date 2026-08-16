package com.example.atocuemobile.ui.screen.timeline.scratch.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
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

// 시간(0~23)을 "01:00" / "AM" 형태로 변환
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
        Column(
            modifier = Modifier.width(56.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = timeText, fontSize = 12.sp, color = Color.Gray)
            Text(text = period, fontSize = 12.sp, color = Color.Gray)
        }

        Column(modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = Color(0xFFEBEBEB), thickness = 1.dp)
            Spacer(modifier = Modifier.height(37.5.dp))

            if (events.isEmpty()) {
                Text(
                    text = "긁음 기록 없음",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight(400),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    events.forEach { event -> ScratchEventItem(event) }
                }
            }

            Spacer(modifier = Modifier.height(37.5.dp))
        }
    }
}