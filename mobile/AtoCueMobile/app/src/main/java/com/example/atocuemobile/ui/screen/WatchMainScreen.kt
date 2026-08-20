package com.example.atocuemobile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.R
import com.example.atocuemobile.ui.theme.Pretendard
import kotlin.random.Random

enum class WatchScreenState {
    LOGIN_MAIN,
    LOGIN_KEYPAD,
    HOME_READY,
    DETECTING,
    WARNING,
    SCRATCH_STOPPED,
    BATTERY_LOW,
    PAUSED
}

@Composable
fun WatchMainScreen(
    initialState: WatchScreenState = WatchScreenState.LOGIN_MAIN,
    onPairingCodeSubmit: (String) -> Unit = {},
    onStartDetection: () -> Unit = {},
    onRestartDetection: () -> Unit = {}
) {
    var currentState by remember { mutableStateOf(initialState) }
    var inputCode by remember { mutableStateOf("") }
    var stoppedMessageIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E)),
        contentAlignment = Alignment.Center
    ) {
        when (currentState) {
            WatchScreenState.LOGIN_MAIN -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.atoqu),
                        contentDescription = "Atoqu 로고",
                        modifier = Modifier
                            .fillMaxWidth(0.66f)
                            .height(42.dp)
                            .offset(y = (-12).dp),
                        contentScale = ContentScale.Fit
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 22.dp)
                    ) {
                        WatchPillButton(
                            text = "연동하기",
                            backgroundColor = Color.White,
                            textColor = Color.Black,
                            onClick = { currentState = WatchScreenState.LOGIN_KEYPAD }
                        )
                    }
                }
            }

            WatchScreenState.LOGIN_KEYPAD -> {
                WatchKeypadScreen(
                    code = inputCode,
                    onNumberClick = { num ->
                        if (inputCode.length < 6) {
                            inputCode += num
                            if (inputCode.length == 6) {
                                onPairingCodeSubmit(inputCode)
                                currentState = WatchScreenState.HOME_READY
                            }
                        }
                    },
                    onDeleteClick = {
                        if (inputCode.isNotEmpty()) {
                            inputCode = inputCode.dropLast(1)
                        }
                    },
                    onClose = {
                        inputCode = ""
                        currentState = WatchScreenState.LOGIN_MAIN
                    }
                )
            }

            WatchScreenState.HOME_READY -> {
                WatchCloudHeroScreen(
                    bubbleImageRes = R.drawable.watch_bubble_hello,
                    bubbleHeight = 32.dp,
                    topPadding = 18.dp,
                    cloudImageRes = R.drawable.watch_cloud_normal,
                    cloudSize = 122.dp,
                    cloudOffsetY = (-8).dp,
                    buttonTopPadding = 84.dp,
                    buttonText = "감지 시작",
                    buttonBgColor = Color.White,
                    buttonTextColor = Color.Black,
                    onButtonClick = {
                        onStartDetection()
                        currentState = WatchScreenState.DETECTING
                    }
                )
            }

            WatchScreenState.DETECTING -> {
                WatchFlowerMessageScreen(
                    flowerImageRes = R.drawable.watch_flower_blue,
                    line1 = "오늘도 좋은",
                    line2 = "하루 보내!",
                    onClick = { currentState = WatchScreenState.WARNING }
                )
            }

            WatchScreenState.WARNING -> {
                WatchFlowerMessageScreen(
                    flowerImageRes = R.drawable.watch_flower_green,
                    line1 = "잠깐만",
                    line2 = "손을 쉬어보자!",
                    onClick = {
                        stoppedMessageIndex = Random.nextInt(2)
                        currentState = WatchScreenState.SCRATCH_STOPPED
                    }
                )
            }

            WatchScreenState.SCRATCH_STOPPED -> {
                val stoppedMessages = listOf(
                    Pair("정말 멋져! 피부가", "편안해질 거야!"),
                    Pair("멈췄네!", "정말 잘했어!!")
                )
                val currentMessage = stoppedMessages[stoppedMessageIndex % stoppedMessages.size]

                WatchFlowerMessageScreen(
                    flowerImageRes = R.drawable.watch_flower_purple,
                    line1 = currentMessage.first,
                    line2 = currentMessage.second,
                    onClick = { currentState = WatchScreenState.DETECTING }
                )
            }

            WatchScreenState.BATTERY_LOW -> {
                WatchCloudHeroScreen(
                    bubbleImageRes = R.drawable.watch_bubble_battery,
                    bubbleHeight = 32.dp,
                    topPadding = 22.dp,
                    cloudImageRes = R.drawable.watch_cloud_sad,
                    cloudSize = 104.dp,
                    cloudOffsetY = (-2).dp,
                    buttonTopPadding = 80.dp,
                    buttonText = "일시 중지",
                    buttonBgColor = Color(0xFF494545),
                    buttonTextColor = Color.White,
                    onButtonClick = { }
                )
            }

            WatchScreenState.PAUSED -> {
                WatchCloudHeroScreen(
                    bubbleImageRes = R.drawable.watch_bubble_restart,
                    bubbleHeight = 32.dp,
                    topPadding = 18.dp,
                    cloudImageRes = R.drawable.watch_cloud_normal,
                    cloudSize = 122.dp,
                    cloudOffsetY = (-8).dp,
                    buttonTopPadding = 84.dp,
                    buttonText = "재시작",
                    buttonBgColor = Color.White,
                    buttonTextColor = Color.Black,
                    onButtonClick = {
                        onRestartDetection()
                        currentState = WatchScreenState.DETECTING
                    }
                )
            }
        }
    }
}

@Composable
private fun WatchCloudHeroScreen(
    bubbleImageRes: Int,
    bubbleHeight: Dp = 32.dp,
    topPadding: Dp = 14.dp,
    cloudImageRes: Int,
    cloudSize: Dp = 122.dp,
    cloudOffsetY: Dp = (-8).dp,
    buttonTopPadding: Dp = 84.dp,
    buttonText: String,
    buttonBgColor: Color,
    buttonTextColor: Color,
    onButtonClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding)
    ) {
        Image(
            painter = painterResource(id = bubbleImageRes),
            contentDescription = "말풍선",
            modifier = Modifier.height(bubbleHeight),
            contentScale = ContentScale.Fit
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = cloudImageRes),
                contentDescription = "구름 캐릭터",
                modifier = Modifier
                    .size(cloudSize)
                    .offset(y = cloudOffsetY),
                contentScale = ContentScale.Fit
            )

            Box(
                modifier = Modifier.padding(top = buttonTopPadding)
            ) {
                WatchPillButton(
                    text = buttonText,
                    backgroundColor = buttonBgColor,
                    textColor = buttonTextColor,
                    onClick = onButtonClick
                )
            }
        }
    }
}

@Composable
private fun WatchFlowerMessageScreen(
    flowerImageRes: Int,
    line1: String,
    line2: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = flowerImageRes),
            contentDescription = null,
            modifier = Modifier.size(160.dp),
            contentScale = ContentScale.Fit
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = line1,
                style = TextStyle(
                    color = Color.White,
                    fontFamily = Pretendard,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 21.sp,
                    textAlign = TextAlign.Center
                )
            )
            Text(
                text = line2,
                style = TextStyle(
                    color = Color.White,
                    fontFamily = Pretendard,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 21.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
private fun WatchKeypadScreen(
    code: String,
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(1.5.dp)
                            .height(14.dp)
                            .background(Color(0xFF3882F6))
                    )

                    Spacer(modifier = Modifier.width(3.dp))

                    Text(
                        text = if (code.isEmpty()) "6자리 코드입력" else code,
                        style = TextStyle(
                            color = if (code.isEmpty()) Color(0xFF6C6E72) else Color.White,
                            fontFamily = Pretendard,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .height(1.dp)
                        .background(Color(0xFF3E4044))
                )
            }

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "닫기",
                tint = Color(0xFFC4C4C4),
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onClose() }
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "⌫")
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            keys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { key ->
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (key.isNotEmpty()) Color(0xFF2C2C2E) else Color.Transparent)
                                .clickable(enabled = key.isNotEmpty()) {
                                    if (key == "⌫") onDeleteClick() else onNumberClick(key)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = key,
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WatchPillButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(104.dp)
            .height(38.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = textColor,
                fontFamily = Pretendard,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 1
        )
    }
}

@Preview(name = "1. 워치 연동 메인", widthDp = 200, heightDp = 200)
@Composable
fun PreviewWatchLoginMain() {
    WatchMainScreen(initialState = WatchScreenState.LOGIN_MAIN)
}

@Preview(name = "2. 워치 코드 입력", widthDp = 200, heightDp = 200)
@Composable
fun PreviewWatchKeypad() {
    WatchMainScreen(initialState = WatchScreenState.LOGIN_KEYPAD)
}

@Preview(name = "3. 감지 시작 전 홈", widthDp = 200, heightDp = 200)
@Composable
fun PreviewWatchHomeReady() {
    WatchMainScreen(initialState = WatchScreenState.HOME_READY)
}

@Preview(name = "4. 감지 측정 중", widthDp = 200, heightDp = 200)
@Composable
fun PreviewWatchDetecting() {
    WatchMainScreen(initialState = WatchScreenState.DETECTING)
}

@Preview(name = "5. 경고 단계", widthDp = 200, heightDp = 200)
@Composable
fun PreviewWatchWarning() {
    WatchMainScreen(initialState = WatchScreenState.WARNING)
}

@Preview(name = "6-1. 멈췄을 때 (멘트 1)", widthDp = 200, heightDp = 200)
@Composable
fun PreviewWatchStopped1() {
    WatchFlowerMessageScreen(
        flowerImageRes = R.drawable.watch_flower_purple,
        line1 = "정말 멋져! 피부가",
        line2 = "편안해질 거야!",
        onClick = {}
    )
}

@Preview(name = "6-2. 멈췄을 때 (멘트 2)", widthDp = 200, heightDp = 200)
@Composable
fun PreviewWatchStopped2() {
    WatchFlowerMessageScreen(
        flowerImageRes = R.drawable.watch_flower_purple,
        line1 = "멈췄네!",
        line2 = "정말 잘했어!!",
        onClick = {}
    )
}

@Preview(name = "7. 배터리 부족 일시중지", widthDp = 200, heightDp = 200)
@Composable
fun PreviewWatchBatteryLow() {
    WatchMainScreen(initialState = WatchScreenState.BATTERY_LOW)
}

@Preview(name = "8. 재시작 화면", widthDp = 200, heightDp = 200)
@Composable
fun PreviewWatchPaused() {
    WatchMainScreen(initialState = WatchScreenState.PAUSED)
}