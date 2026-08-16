package com.example.scratchdetecter.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Text

@Composable
fun FlowerMessage(
    color: Color,
    message: String,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .size(176.dp)
            .then(
                if (onLongPress != null) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(onLongPress = { onLongPress() })
                    }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        val petalSize = 68.dp
        val petalColor = color

        Box(Modifier.size(petalSize).offset(y = (-47).dp).background(petalColor, CircleShape))
        Box(Modifier.size(petalSize).offset(x = 41.dp, y = (-24).dp).background(petalColor, CircleShape))
        Box(Modifier.size(petalSize).offset(x = 41.dp, y = 24.dp).background(petalColor, CircleShape))
        Box(Modifier.size(petalSize).offset(y = 47.dp).background(petalColor, CircleShape))
        Box(Modifier.size(petalSize).offset(x = (-41).dp, y = 24.dp).background(petalColor, CircleShape))
        Box(Modifier.size(petalSize).offset(x = (-41).dp, y = (-24).dp).background(petalColor, CircleShape))

        Box(
            modifier = Modifier.size(106.dp).background(petalColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                color = Color.White,
                fontSize = 18.sp,
                lineHeight = 26.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
