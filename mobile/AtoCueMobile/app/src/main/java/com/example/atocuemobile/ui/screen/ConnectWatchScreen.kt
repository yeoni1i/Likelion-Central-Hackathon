package com.example.atocuemobile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.theme.AtoCueMobileTheme
import com.example.atocuemobile.ui.theme.Pretendard

private val BaseBackgroundColor = Color(0xFFF6F7FB)
private val NumberCardBorderColor = Color(0xFFEBEBEB)

/**
 * 워치 연결 인증 번호 화면
 */
@Composable
fun ConnectWatchScreen(
    title: String = "워치연결",
    code: String = "12345",
    onBackClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(), // 상단 상태바 영역 확보
        color = BaseBackgroundColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 상단 앱바
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = Pretendard,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 30.sp,
                        color = Color(0xFF000000)
                    )
                )
            }

            Spacer(modifier = Modifier.height(90.dp))

            // 중앙 안내 문구 (글씨 크기 및 굵기 확대: 20sp -> 22sp)
            Text(
                text = "아래의 코드를 아이의\n워치에 입력해주세요",
                style = TextStyle(
                    fontFamily = Pretendard,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 33.sp,
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(36.dp))

            // 5자리 숫자 카드 영역
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val digits = code.padEnd(5, ' ').take(5).toCharArray()

                digits.forEachIndexed { index, char ->
                    NumberCard(number = char.toString())
                    if (index < digits.size - 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}

/**
 * 개별 숫자 프레임 카드 (크기 확대)
 */
@Composable
private fun NumberCard(number: String) {
    Box(
        modifier = Modifier
            .size(width = 56.dp, height = 68.dp) // 카드 전체 크기 확장
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color(0x0D000000)
            )
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, NumberCardBorderColor, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number,
            style = TextStyle(
                fontFamily = Pretendard,
                fontSize = 38.sp, // 내부 숫자 폰트 크기 확대 (35sp -> 38sp)
                fontWeight = FontWeight.Medium,
                lineHeight = 52.sp,
                color = Color(0xFF000000),
                textAlign = TextAlign.Center
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ConnectWatchScreenPreview() {
    AtoCueMobileTheme {
        ConnectWatchScreen(code = "12345")
    }
}