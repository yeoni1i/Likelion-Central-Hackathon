package com.example.scratchdetecter.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.scratchdetecter.R
import com.example.scratchdetecter.presentation.component.CharacterScreen

@Composable
fun BatteryLowScreen(
    onPause: () -> Unit
) {
    CharacterScreen(
        message = "배터리가 부족해",
        buttonText = "일시 중지",
        onButtonClick = onPause,
        imageResId = R.drawable.atocue_character_sad,
        bubbleWidthDp = 94,
        messageFontSizeSp = 11,
        buttonContainerColor = Color(0xFF514E50),
        buttonContentColor = Color.White
    )
}