package com.example.scratchdetecter.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.scratchdetecter.presentation.component.FlowerMessage

@Composable
fun MonitoringScreen(onStop: () -> Unit) {
    val stableMessages = listOf(
        "오늘도 충분히\n잘하고 있어",
        "오늘도 행복한 일이\n많을 거야!",
        "좋은 하루\n보내자!!",
        "언제나\n네 편이야"
    )

    val randomMessage = remember {
        stableMessages.random()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        FlowerMessage(
            color = Color(0xFF424966),
            message = randomMessage,
            onLongPress = onStop
        )
    }
}
