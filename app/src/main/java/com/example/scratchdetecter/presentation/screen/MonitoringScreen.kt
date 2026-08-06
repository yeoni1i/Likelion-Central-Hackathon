package com.example.scratchdetecter.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.scratchdetecter.presentation.component.FlowerMessage

@Composable
fun MonitoringScreen(onStop: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        FlowerMessage(
            color = Color(0xFF424966),
            message = "오늘도 좋은\n하루 보내!",
            onLongPress = onStop
        )
    }
}
