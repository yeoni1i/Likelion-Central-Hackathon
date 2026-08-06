package com.example.scratchdetecter.presentation.screen

import androidx.compose.runtime.Composable
import com.example.scratchdetecter.R
import com.example.scratchdetecter.presentation.component.CharacterScreen

@Composable
fun RestartScreen(
    onRestart: () -> Unit
) {
    CharacterScreen(
        message = "다시 시작해보자!",
        buttonText = "재시작",
        onButtonClick = onRestart,
        imageResId = R.drawable.atocue_character,
        bubbleWidthDp = 94,
        messageFontSizeSp = 11
    )
}