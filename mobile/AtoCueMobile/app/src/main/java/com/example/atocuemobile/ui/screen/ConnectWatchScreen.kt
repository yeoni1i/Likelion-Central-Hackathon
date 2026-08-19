package com.example.atocuemobile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.theme.AtoCueMobileTheme

private val BaseBackgroundColor = Color(0xFFF6F7FB)
private val NumberCardBorderColor = Color(0xFFEBEBEB)
private val PrimaryBlueColor = Color(0xFF5398FF)
private val GrayTextColor = Color(0xFF8E95A3)
private val Pretendard = FontFamily.Default

@Composable
fun ConnectWatchScreen(
    title: String = "기기 연결",
    code: String = "------",
    isLoading: Boolean = false,
    onRefreshCode: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onSkipClick: () -> Unit = {} // 💡 워치 연결 건너뛰기 콜백 추가
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BaseBackgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // 1. 상단 앱바
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

            Spacer(modifier = Modifier.height(40.dp))

            // 2. 중앙 안내 문구 (두께 Normal로 조정됨)
            Text(
                text = "아래의 코드를 아이의\n워치에 입력해주세요",
                style = TextStyle(
                    fontFamily = Pretendard,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 33.sp,
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(36.dp))

            // 3. 6자리 숫자 카드 영역
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = PrimaryBlueColor,
                        strokeWidth = 3.dp
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val cleanCode = code.filter { it.isDigit() }.padEnd(6, '-').take(6)
                    val digits = cleanCode.toCharArray()

                    digits.forEachIndexed { index, char ->
                        NumberCard(number = char.toString())
                        if (index < digits.size - 1) {
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 4. 유효시간 안내 및 재발급 버튼
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "코드는 5분간 유효합니다",
                    style = TextStyle(
                        fontFamily = Pretendard,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = GrayTextColor
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true)
                        ) {
                            onRefreshCode()
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "재발급",
                        tint = PrimaryBlueColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "코드 재발급",
                        style = TextStyle(
                            fontFamily = Pretendard,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlueColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 5. [개발용] 워치 연결 건너뛰기 버튼 (하단 고정 배치)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onSkipClick),
                    color = PrimaryBlueColor
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "⚡ [개발용] 워치 연결 건너뛰기",
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberCard(number: String) {
    Box(
        modifier = Modifier
            .size(width = 48.dp, height = 64.dp)
            .shadow(
                elevation = 16.dp,
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
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium,
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
        ConnectWatchScreen(code = "849201")
    }
}