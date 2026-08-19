package com.example.atocuemobile.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val ProgressBlue = Color(0xFF5398FF)
private val ProgressTrackGray = Color(0xFFE3E6EA)

@Composable
fun OnboardingProgressBar(step: Int, totalSteps: Int) {
    val progress = (step.toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(6.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(ProgressTrackGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = progress)
                .clip(RoundedCornerShape(4.dp))
                .background(ProgressBlue)
        )
    }
}