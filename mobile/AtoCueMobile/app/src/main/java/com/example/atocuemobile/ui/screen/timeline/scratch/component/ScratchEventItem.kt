package com.example.atocuemobile.ui.screen.timeline.scratch.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.atocuemobile.ui.screen.timeline.model.ScratchEvent
import com.example.atocuemobile.ui.screen.timeline.model.ScratchLevel
import androidx.compose.ui.unit.dp
import com.example.atocuemobile.ui.screen.timeline.LevelCaution
import com.example.atocuemobile.ui.screen.timeline.LevelDanger
import com.example.atocuemobile.ui.screen.timeline.LevelNormal
import com.example.atocuemobile.ui.screen.timeline.LevelStable


@Composable
fun ScratchEventItem(event: ScratchEvent) {
    val (emoji, label, color) = when (event.level) {
        ScratchLevel.STABLE -> Triple("🙂", "안정", LevelStable)
        ScratchLevel.NORMAL -> Triple("🙂", "보통", LevelNormal)
        ScratchLevel.CAUTION -> Triple("😐", "주의", LevelCaution)
        ScratchLevel.DANGER -> Triple("😣", "위험", LevelDanger)
    }
    Text(text = "$emoji $label", color = color)

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "$emoji $label")
            Text(text = "발생시각 | ${event.startTime}~${event.endTime}")
            // TODO: 회색 작은 글씨 스타일 지정
        }
        Text(text = "${event.durationMinutes}분 지속")
    }
}