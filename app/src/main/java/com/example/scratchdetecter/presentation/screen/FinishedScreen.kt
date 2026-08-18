package com.example.scratchdetecter.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.scratchdetecter.detection.FINISHED_DISPLAY_MS
import com.example.scratchdetecter.presentation.component.FlowerMessage
import kotlinx.coroutines.delay

@Composable
fun FinishedScreen(
    onTimeout: () -> Unit
) {
    val finishedMessages = listOf(
        "지금처럼\n천천히 해보자",
        "멈췄네!\n정말 잘했어",
        "스스로 멈춘 거\n정말 멋져",
        "지금처럼 천천히\n해도 괜찮아"
    )

    val randomMessage = remember {
        finishedMessages.random()
    }

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
            message = randomMessage
        )
    }
}