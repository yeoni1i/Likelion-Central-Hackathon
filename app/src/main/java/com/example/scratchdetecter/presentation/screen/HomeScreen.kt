package com.example.scratchdetecter.presentation.screen

import androidx.compose.runtime.Composable
import com.example.scratchdetecter.R
import com.example.scratchdetecter.presentation.component.CharacterScreen

@Composable
fun HomeScreen(
    onStart: () -> Unit
) {
    CharacterScreen(
        message = "안녕!",
        buttonText = "감지 시작",
        onButtonClick = onStart,
        imageResId = R.drawable.atocue_character
    )
}