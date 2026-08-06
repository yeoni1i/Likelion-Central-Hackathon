package com.example.scratchdetecter.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Text
import com.example.scratchdetecter.R

@Composable
fun MascotCharacter(
    message: String,
    modifier: Modifier = Modifier,
    imageSizeDp: Int = 132
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .drawBehind {
                    val triangleWidth = 14.dp.toPx()
                    val triangleHeight = 10.dp.toPx()
                    val centerX = size.width / 2f
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(centerX - triangleWidth / 2f, size.height)
                        lineTo(centerX + triangleWidth / 2f, size.height)
                        lineTo(centerX, size.height + triangleHeight)
                        close()
                    }
                    drawPath(path, Color(0xFF514E50))
                }
                .background(Color(0xFF514E50), RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(10.dp))

        Image(
            painter = painterResource(R.drawable.atocue_character),
            contentDescription = "AtoCue 캐릭터",
            modifier = Modifier.size(imageSizeDp.dp),
            contentScale = ContentScale.Fit
        )
    }
}
