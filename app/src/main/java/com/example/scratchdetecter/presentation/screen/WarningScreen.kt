package com.example.scratchdetecter.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.scratchdetecter.presentation.component.FlowerMessage
import kotlinx.coroutines.delay

@Composable
fun WarningScreen(
    onTimeout: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(3_000L)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181818)),
        contentAlignment = Alignment.Center
    ) {
        FlowerMessage(
            color = Color(0xFF31593C),
            message = "잠깐만\n손을 쉬어보자!"
        )
    }
}