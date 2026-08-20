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

    // 워치 원형 화면 기준 크기 조정
    imageSizeDp: Int = 92,
    bubbleWidthDp: Int = 54,
    bubbleHeightDp: Int = 28,
    messageFontSizeSp: Int = 12,

    buttonWidthDp: Int = 102,
    buttonHeightDp: Int = 40,

    buttonContainerColor: Color = Color.White,
    buttonContentColor: Color = Color.Black
) {

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        /*
         * 기존 TopCenter 배치를 제거.
         *
         * 원형 워치에서는 위쪽 가용 폭이 좁기 때문에
         * 캐릭터를 TopCenter에 바로 붙이면
         * 말풍선/이미지가 잘릴 수 있음.
         *
         * 화면 중앙에서 약간 위로 올리는 방식으로 변경.
         */
        MascotCharacter(
            message = message,
            imageSizeDp = imageSizeDp,
            bubbleWidthDp = bubbleWidthDp,
            bubbleHeightDp = bubbleHeightDp,
            messageFontSizeSp = messageFontSizeSp,
            imageResId = imageResId,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-28).dp)
        )

        /*
         * 버튼은 하단 원형 가장자리에 너무 붙지 않도록
         * 24dp 위로 이동.
         */
        Button(
            onClick = onButtonClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-24).dp)
                .width(buttonWidthDp.dp)
                .height(buttonHeightDp.dp),
            shape = RoundedCornerShape(
                buttonHeightDp.dp / 2
            ),
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