package com.example.scratchdetecter.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.scratchdetecter.presentation.component.FlowerMessage
import kotlinx.coroutines.delay

@Composable
fun WarningScreen(
    onTimeout: () -> Unit
) {
    val warningMessages = listOf(
        "천천히 멈춰보자,\n할 수 있어",
        "많이 불편하지?\n손을 잠깐 쉬어볼까?",
        "괜찮아,\n네 잘못이 아닌걸",
        "많이 힘들지?\n내가 응원할게",
        "천천히 손을\n내려볼까?",
        "지금도 충분히\n잘하고 있어"
    )

    val randomMessage = remember {
        warningMessages.random()
    }

    LaunchedEffect(Unit) {
        delay(1_000L)
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
            message = randomMessage
        )
    }
}