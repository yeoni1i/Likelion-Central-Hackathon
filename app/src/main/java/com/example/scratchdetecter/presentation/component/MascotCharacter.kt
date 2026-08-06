package com.example.scratchdetecter.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Text
import com.example.scratchdetecter.R
import androidx.annotation.DrawableRes

@Composable
fun MascotCharacter(
    message: String,
    modifier: Modifier = Modifier,
    imageSizeDp: Int = 99,
    bubbleWidthDp: Int = 54,
    bubbleHeightDp: Int = 28,
    messageFontSizeSp: Int = 12,
    @DrawableRes imageResId: Int = R.drawable.atocue_character
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(bubbleWidthDp.dp)
                .height(bubbleHeightDp.dp)
                .drawBehind {
                    val triangleWidth = 9.dp.toPx()
                    val triangleHeight = 6.dp.toPx()
                    val centerX = size.width / 2f

                    val trianglePath = Path().apply {
                        moveTo(
                            centerX - triangleWidth / 2f,
                            size.height - 1.dp.toPx()
                        )
                        lineTo(
                            centerX + triangleWidth / 2f,
                            size.height - 1.dp.toPx()
                        )
                        lineTo(
                            centerX,
                            size.height + triangleHeight
                        )
                        close()
                    }

                    drawPath(
                        path = trianglePath,
                        color = Color(0xFF514E50)
                    )
                }
                .background(
                    color = Color(0xFF514E50),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                color = Color.White,
                fontSize = messageFontSizeSp.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(imageResId),
            contentDescription = "AtoCue 캐릭터",
            modifier = Modifier.size(imageSizeDp.dp)
        )
    }
}
