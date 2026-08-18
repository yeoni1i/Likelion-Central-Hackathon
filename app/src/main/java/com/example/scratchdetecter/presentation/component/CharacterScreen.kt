package com.example.scratchdetecter.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Text

@Composable
fun CharacterScreen(
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    @DrawableRes imageResId: Int,
    modifier: Modifier = Modifier,
    imageSizeDp: Int = 107,
    bubbleWidthDp: Int = 54,
    bubbleHeightDp: Int = 28,
    messageFontSizeSp: Int = 12,
    characterOffsetYDp: Int = 15,
    buttonWidthDp: Int = 102,
    buttonHeightDp: Int = 40,
    buttonOffsetYDp: Int = -20,
    buttonContainerColor: Color = Color.White,
    buttonContentColor: Color = Color.Black
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        MascotCharacter(
            message = message,
            imageSizeDp = imageSizeDp,
            bubbleWidthDp = bubbleWidthDp,
            bubbleHeightDp = bubbleHeightDp,
            messageFontSizeSp = messageFontSizeSp,
            imageResId = imageResId,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = characterOffsetYDp.dp)
        )

        Button(
            onClick = onButtonClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = buttonOffsetYDp.dp)
                .width(buttonWidthDp.dp)
                .height(buttonHeightDp.dp),
            shape = RoundedCornerShape(buttonHeightDp.dp / 2),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonContainerColor,
                contentColor = buttonContentColor
            )
        ) {
            Text(
                text = buttonText,
                modifier = Modifier.fillMaxWidth(),
                color = buttonContentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
