package com.example.scratchdetecter.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.scratchdetecter.detection.FINISHED_DISPLAY_MS
import com.example.scratchdetecter.presentation.component.FlowerMessage
import kotlinx.coroutines.delay

@Composable
fun FinishedScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(FINISHED_DISPLAY_MS)
        onTimeout()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        FlowerMessage(
            color = Color(0xFF60384F),
            message = "정말 멋져! 피부가\n편안해질 거야!"
        )
    }
}
