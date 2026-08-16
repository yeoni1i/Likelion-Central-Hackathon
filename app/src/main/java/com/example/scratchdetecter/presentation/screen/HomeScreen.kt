package com.example.scratchdetecter.presentation.screen

import androidx.compose.runtime.Composable
import com.example.scratchdetecter.R
import com.example.scratchdetecter.presentation.component.CharacterScreen

@Composable
fun HomeScreen(
    buttonText: String,
    onStart: () -> Unit
) {
    CharacterScreen(
        message =
            if (buttonText == "재시작") {
                "다시 시작해보자!"
            } else {
                "안녕!"
            },
        buttonText = buttonText,
        onButtonClick = onStart,
        imageResId = R.drawable.atocue_character
    )
}
