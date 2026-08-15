package com.example.atocuemobile.ui.screen

import android.graphics.Color as AndroidColor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import com.example.atocuemobile.R
import com.example.atocuemobile.network.dto.WeatherResponse
import com.example.atocuemobile.ui.component.BottomNavTab
import com.example.atocuemobile.ui.component.BottomNavigationBar
import com.example.atocuemobile.ui.model.ScratchStatus
import com.example.atocuemobile.ui.theme.AtoCueMobileTheme
import com.example.atocuemobile.ui.theme.LabelGray
import com.example.atocuemobile.ui.theme.Pretendard
import com.example.atocuemobile.ui.theme.PrimaryBlue

private val BaseBackgroundColor = Color(0xFFF6F7FB)
private val DarkButtonBg = Color(0xFF36383D)
private val DisabledButtonBg = Color(0xFF36383D)
private val GrayScaleG6 = Color(0xFF9E9E9E)
private val TitleBlackColor = Color(0xFF212121)
private val SubtitleBlackColor = Color(0xFF121212)
private val DateGrayColor = Color(0xFF999999)
private val WeatherCardBorderColor = Color(0xFFF3F4F6)
private val InfoBoxBgColor = Color(0xFFF2F4F7)
private val LightTextGray = Color(0xFF9E9E9E)

// 기기 연결 전 칩
private val DisconnectedChipBg = Color(0xFFD0D6DD)
private val DisconnectedChipDot = Color(0xFF6C6E72)

// 기기 연결 후 칩
private val ConnectedChipBg = Color(0x265398FF)
private val ConnectedBlueColor = Color(0xFF397FE9)

private val ActiveDotColor = Color(0xFF5398FF)
private val InactiveDotColor = Color(0xFFD9D9D9)

// 감지 상태 열거형
enum class DetectionState {
    NOT_CONNECTED, // 기기 연결 전
    READY,         // 감지 시작 전 (start.png)
    DETECTING,     // 실시간 감지 중 (구름 5단계)
    PAUSED,        // 감지 일시중지 (stop.png)
    BATTERY_LOW    // 배터리 부족으로 중지 (battery.png)
}

data class GuideMessage(
    val title: String,
    val description: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    isDeviceConnected: Boolean = true,
    detectionState: DetectionState = DetectionState.DETECTING,
    currentStatus: ScratchStatus = ScratchStatus.STABLE,
    totalScratchSeconds: Int = 0,
    timeCriteriaText: String = "00월 00일 (수) | 00시 기준",
    weatherData: WeatherResponse? = null,
    guideList: List<GuideMessage> = listOf(
        GuideMessage("가이드 제목 1", "추후 백엔드 API 연동으로 맞춤 메시지가 표시되는 공간입니다."),
        GuideMessage("가이드 제목 2", "추후 백엔드 API 연동으로 맞춤 메시지가 표시되는 공간입니다."),
        GuideMessage("가이드 제목 3", "추후 백엔드 API 연동으로 맞춤 메시지가 표시되는 공간입니다.")
    ),
    onConnectWatchClick: () -> Unit = {},
    onStartDetectionClick: () -> Unit = {},
    onStopDetectionClick: () -> Unit = {},
    onRestartDetectionClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onLifeLogClick: () -> Unit = {},  // 생활기록 클릭 콜백
    onMealLogClick: () -> Unit = {},  // 식단기록 클릭 콜백
    selectedTab: BottomNavTab = BottomNavTab.HOME,
    onTabSelected: (BottomNavTab) -> Unit = {}
) {
    var showConnectModal by remember { mutableStateOf(false) }
    var isFabExpanded by remember { mutableStateOf(false) } // FAB 확장 여부 상태

    // FAB 회전 애니메이션 (+ -> X)
    val fabRotation by animateFloatAsState(
        targetValue = if (isFabExpanded) 45f else 0f,
        label = "fabRotation"
    )

    val effectiveConnected = isDeviceConnected && (detectionState != DetectionState.NOT_CONNECTED && detectionState != DetectionState.BATTERY_LOW)

    val tempText = weatherData?.let { "${it.temperature.toInt()}도" } ?: "--도"
    val humidityText = weatherData?.let { "${it.humidity}%" } ?: "--%"
    val airQualityText = weatherData?.airQuality ?: "--"

    val fineDustIconRes = when (airQualityText) {
        "매우 좋음", "매우좋음" -> R.drawable.verygood
        "좋음" -> R.drawable.good
        "보통" -> R.drawable.normal
        "나쁨" -> R.drawable.bad
        "매우 나쁨", "매우나쁨" -> R.drawable.verybad
        else -> R.drawable.verygood
    }

    val pagerState = rememberPagerState(pageCount = { guideList.size })

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        },
        containerColor = BaseBackgroundColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. 상단 헤더
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.atocue),
                        contentDescription = "AtoCue Logo",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier.height(16.dp)
                    )

                    Surface(
                        color = if (effectiveConnected) ConnectedChipBg else DisconnectedChipBg,
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        if (effectiveConnected) ConnectedBlueColor else DisconnectedChipDot,
                                        CircleShape
                                    )
                            )
                            Text(
                                text = if (effectiveConnected) "기기 연결" else "기기 연결 없음",
                                style = TextStyle(
                                    fontFamily = Pretendard,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (effectiveConnected) ConnectedBlueColor else TitleBlackColor
                                )
                            )
                        }
                    }
                }

                // 2. 히어로 영역
                when {
                    !isDeviceConnected || detectionState == DetectionState.NOT_CONNECTED -> {
                        // [상태 0: 기기 연결 전]
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 30.dp, end = 30.dp, top = 120.dp, bottom = 120.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "워치를 연결해주세요",
                                style = TextStyle(
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 22.sp,
                                    color = TitleBlackColor
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "연결된 워치가 없습니다.\n연결상태를 확인해주세요",
                                style = TextStyle(
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    lineHeight = 21.sp,
                                    color = TitleBlackColor,
                                    textAlign = TextAlign.Center
                                )
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    showConnectModal = true
                                    onConnectWatchClick()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkButtonBg),
                                shape = RoundedCornerShape(100.dp),
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(52.dp)
                            ) {
                                Text(
                                    text = "워치 연결하기",
                                    style = TextStyle(
                                        fontFamily = Pretendard,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 18.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    detectionState == DetectionState.READY -> {
                        // [상태 1: 기기 연결 완료 & 감지 시작 전]
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 30.dp, end = 30.dp, top = 20.dp, bottom = 20.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    spotColor = Color(0x0A000000)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(387.dp)
                                    .padding(horizontal = 20.dp, vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "오늘의 감지를 시작해보세요",
                                        style = TextStyle(
                                            fontFamily = Pretendard,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 22.sp,
                                            color = TitleBlackColor
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "하단의 버튼을 눌러 감지를 시작해보세요",
                                        style = TextStyle(
                                            fontFamily = Pretendard,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 14.sp,
                                            color = SubtitleBlackColor
                                        )
                                    )
                                }

                                Image(
                                    painter = painterResource(id = R.drawable.start),
                                    contentDescription = "감지 시작 아이콘",
                                    modifier = Modifier.size(195.dp),
                                    contentScale = ContentScale.Fit
                                )

                                Button(
                                    onClick = onStartDetectionClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkButtonBg),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                ) {
                                    Text(
                                        text = "감지 시작",
                                        style = TextStyle(
                                            fontFamily = Pretendard,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 17.sp,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                }
                            }
                        }
                    }

                    detectionState == DetectionState.PAUSED -> {
                        // [상태 2: 감지 일시중지]
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 30.dp, end = 30.dp, top = 20.dp, bottom = 20.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    spotColor = Color(0x0A000000)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(387.dp)
                                    .padding(horizontal = 20.dp, vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "일시중지 되었습니다.",
                                        style = TextStyle(
                                            fontFamily = Pretendard,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 22.sp,
                                            color = TitleBlackColor
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "현재 감시가 일시중지 되었습니다",
                                        style = TextStyle(
                                            fontFamily = Pretendard,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 14.sp,
                                            color = SubtitleBlackColor
                                        )
                                    )
                                }

                                Image(
                                    painter = painterResource(id = R.drawable.stop),
                                    contentDescription = "일시중지 아이콘",
                                    modifier = Modifier.size(220.dp),
                                    contentScale = ContentScale.Fit
                                )

                                Button(
                                    onClick = onRestartDetectionClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkButtonBg),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                ) {
                                    Text(
                                        text = "재시작",
                                        style = TextStyle(
                                            fontFamily = Pretendard,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 17.sp,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                }
                            }
                        }
                    }

                    detectionState == DetectionState.BATTERY_LOW -> {
                        // [상태 3: 배터리 부족]
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 30.dp, end = 30.dp, top = 20.dp, bottom = 20.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    spotColor = Color(0x0A000000)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(387.dp)
                                    .padding(horizontal = 20.dp, vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "워치 배터리를 확인해주세요",
                                        style = TextStyle(
                                            fontFamily = Pretendard,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 22.sp,
                                            color = TitleBlackColor
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "배터리 부족으로 인해 감지가 중지되었습니다.",
                                        style = TextStyle(
                                            fontFamily = Pretendard,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 14.sp,
                                            color = SubtitleBlackColor
                                        )
                                    )
                                }

                                Image(
                                    painter = painterResource(id = R.drawable.battery),
                                    contentDescription = "배터리 부족 아이콘",
                                    modifier = Modifier.size(195.dp),
                                    contentScale = ContentScale.Fit
                                )

                                Button(
                                    onClick = { },
                                    enabled = false,
                                    colors = ButtonDefaults.buttonColors(
                                        disabledContainerColor = DisabledButtonBg,
                                        disabledContentColor = GrayScaleG6
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                ) {
                                    Text(
                                        text = "일시 정지 됨",
                                        style = TextStyle(
                                            fontFamily = Pretendard,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 17.sp,
                                            color = GrayScaleG6,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        // [상태 4: 실시간 감지 중]
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 30.dp, end = 30.dp, top = 20.dp, bottom = 20.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    spotColor = Color(0x0A000000)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(387.dp)
                                    .padding(horizontal = 20.dp, vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // 1. 상단 타이틀 & 날짜시간
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = currentStatus.heroTitle,
                                        style = TextStyle(
                                            fontFamily = Pretendard,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 22.sp,
                                            color = TitleBlackColor
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = timeCriteriaText,
                                            style = TextStyle(
                                                fontFamily = Pretendard,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 14.sp,
                                                color = DateGrayColor
                                            )
                                        )

                                        Spacer(modifier = Modifier.width(6.dp))

                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(CircleShape)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = ripple(bounded = true, radius = 13.dp)
                                                ) {
                                                    onRefreshClick()
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.refresh),
                                                contentDescription = "새로고침",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                // 2. 중앙 구름 캐릭터 + 배지 영역
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = currentStatus.cloudImageRes),
                                        contentDescription = currentStatus.badgeLabel,
                                        modifier = Modifier.size(145.dp),
                                        contentScale = ContentScale.Fit
                                    )

                                    Spacer(modifier = Modifier.height(26.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Surface(
                                            color = currentStatus.badgeBgColor,
                                            shape = RoundedCornerShape(5.dp)
                                        ) {
                                            Text(
                                                text = currentStatus.badgeLabel,
                                                style = TextStyle(
                                                    fontFamily = Pretendard,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 16.sp,
                                                    lineHeight = 24.sp,
                                                    color = currentStatus.badgeTextColor,
                                                    textAlign = TextAlign.Center
                                                ),
                                                modifier = Modifier.padding(
                                                    horizontal = if (currentStatus == ScratchStatus.VERY_DANGER) 5.dp else 10.dp,
                                                    vertical = 2.dp
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = "긁음 ${totalScratchSeconds}초 지속",
                                            style = TextStyle(
                                                fontFamily = Pretendard,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 18.sp,
                                                color = Color(0xFF000000)
                                            )
                                        )
                                    }
                                }

                                // 3. 하단 와이드 감지 중지 버튼
                                Button(
                                    onClick = onStopDetectionClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkButtonBg),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                ) {
                                    Text(
                                        text = "감지 중지",
                                        style = TextStyle(
                                            fontFamily = Pretendard,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 17.sp,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. 오늘의 날씨 섹션
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 25.dp,
                            shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
                            spotColor = Color(0x0D000000)
                        ),
                    shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 28.dp)
                    ) {
                        Text(
                            text = "오늘의 날씨",
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TitleBlackColor
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            WeatherCard(
                                modifier = Modifier.weight(1f),
                                title = tempText,
                                subtitle = "온도",
                                iconRes = R.drawable.temp,
                                iconSize = 24.dp
                            )
                            WeatherCard(
                                modifier = Modifier.weight(1f),
                                title = humidityText,
                                subtitle = "습도",
                                iconRes = R.drawable.water,
                                iconSize = 22.dp
                            )
                            WeatherCard(
                                modifier = Modifier.weight(1f),
                                title = airQualityText,
                                subtitle = "미세먼지",
                                iconRes = fineDustIconRes,
                                iconSize = 26.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (guideList.isNotEmpty()) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxWidth()
                            ) { page ->
                                val item = guideList[page]
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(InfoBoxBgColor, RoundedCornerShape(16.dp))
                                        .padding(18.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = item.title,
                                            style = TextStyle(
                                                fontFamily = Pretendard,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = TitleBlackColor
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = item.description,
                                            style = TextStyle(
                                                fontFamily = Pretendard,
                                                fontSize = 12.sp,
                                                lineHeight = 18.sp,
                                                color = LabelGray
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(guideList.size) { index ->
                                    val isSelected = pagerState.currentPage == index
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 3.dp)
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) ActiveDotColor else InactiveDotColor)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. 오늘의 경고 타임라인 섹션
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RectangleShape,
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "오늘의 경고 타임라인",
                                style = TextStyle(
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = TitleBlackColor
                                )
                            )

                            Text(
                                text = "더보기",
                                style = TextStyle(
                                    fontFamily = Pretendard,
                                    fontSize = 13.sp,
                                    color = LabelGray
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        if (isDeviceConnected) {
                            TimelineSectionGroup(time = "11:00\nAM") {
                                TimelineCard(status = "안정", duration = "00분", iconRes = R.drawable.verygood)
                                Spacer(modifier = Modifier.height(10.dp))
                                TimelineCard(status = "보통", duration = "00분", iconRes = R.drawable.good)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            TimelineSectionGroup(time = "10:00\nAM") {
                                TimelineCard(status = "주의", duration = "00분", iconRes = R.drawable.bad)
                                Spacer(modifier = Modifier.height(10.dp))
                                TimelineCard(status = "보통", duration = "00분", iconRes = R.drawable.good)
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "긁음 기록이 없습니다.",
                                    style = TextStyle(
                                        fontFamily = Pretendard,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = LightTextGray
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // [FAB 확장 시 나타나는 어두운 오버레이 배경]
            AnimatedVisibility(
                visible = isFabExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x80000000)) // 반투명 블랙 오버레이
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isFabExpanded = false
                        }
                )
            }

            // [우측 하단 확장형 플로팅 메뉴 (Speed Dial FAB)]
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. 생활기록 & 식단기록 펼침 메뉴
                AnimatedVisibility(
                    visible = isFabExpanded,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // [메뉴 1: 생활기록]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "생활기록",
                                style = TextStyle(
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            )

                            FloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    onLifeLogClick()
                                },
                                containerColor = Color(0xFFFFFFFF),
                                contentColor = Color.Unspecified,
                                shape = CircleShape,
                                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                                modifier = Modifier.size(52.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.book),
                                    contentDescription = "생활기록",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // [메뉴 2: 식단기록]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "식단기록",
                                style = TextStyle(
                                    fontFamily = Pretendard,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            )

                            FloatingActionButton(
                                onClick = {
                                    isFabExpanded = false
                                    onMealLogClick()
                                },
                                containerColor = Color(0xFFFFFFFF),
                                contentColor = Color.Unspecified,
                                shape = CircleShape,
                                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                                modifier = Modifier.size(52.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.meal),
                                    contentDescription = "식단기록",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                // 2. 메인 토글 FAB (+ / X 회전 버튼)
                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded },
                    containerColor = PrimaryBlue,
                    contentColor = Color.White,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = if (isFabExpanded) "닫기" else "기록 추가",
                        modifier = Modifier.rotate(fabRotation)
                    )
                }
            }
        }
    }

    // 워치 연결 모달
    if (showConnectModal) {
        Dialog(
            onDismissRequest = { showConnectModal = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            val dialogWindowProvider = LocalView.current.parent as? DialogWindowProvider
            SideEffect {
                dialogWindowProvider?.window?.let { window ->
                    window.setBackgroundDrawable(
                        android.graphics.drawable.ColorDrawable(AndroidColor.TRANSPARENT)
                    )
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    @Suppress("DEPRECATION")
                    window.statusBarColor = AndroidColor.TRANSPARENT
                    @Suppress("DEPRECATION")
                    window.navigationBarColor = AndroidColor.TRANSPARENT
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        window.attributes = window.attributes.apply {
                            layoutInDisplayCutoutMode =
                                android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                        }
                    }
                    window.setDimAmount(0f)
                }
            }

            ConnectWatchScreen(
                code = "12345",
                onBackClick = { showConnectModal = false }
            )
        }
    }
}

@Composable
private fun WeatherCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    iconRes: Int,
    iconSize: Dp = 26.dp
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0x0D000000)
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(1.dp, WeatherCardBorderColor, RoundedCornerShape(16.dp))
            .padding(top = 20.dp, bottom = 16.dp, start = 4.dp, end = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = subtitle,
                modifier = Modifier.size(iconSize)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = Pretendard,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF000000)
                ),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = TextStyle(
                    fontFamily = Pretendard,
                    fontSize = 13.sp,
                    color = LabelGray
                )
            )
        }
    }
}

@Composable
private fun TimelineSectionGroup(
    time: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = time,
            style = TextStyle(
                fontFamily = Pretendard,
                fontSize = 12.sp,
                color = LabelGray,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.width(42.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            content()
        }
    }
}

@Composable
private fun TimelineCard(
    status: String,
    duration: String,
    iconRes: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = status,
                        style = TextStyle(
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = TitleBlackColor
                        )
                    )
                    Text(
                        text = "발생시각 | 00:00~00:00",
                        style = TextStyle(
                            fontFamily = Pretendard,
                            fontSize = 11.sp,
                            color = LabelGray
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = duration,
                    style = TextStyle(
                        fontFamily = Pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TitleBlackColor
                    )
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "지속",
                    style = TextStyle(
                        fontFamily = Pretendard,
                        fontSize = 12.sp,
                        color = LabelGray
                    )
                )
            }
        }
    }
}

@Preview(name = "1. 기기 연결 전", showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenDisconnectedPreview() {
    AtoCueMobileTheme {
        HomeScreen(isDeviceConnected = false, detectionState = DetectionState.NOT_CONNECTED)
    }
}

@Preview(name = "2. 감지 시작 전", showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenStartReadyPreview() {
    AtoCueMobileTheme {
        HomeScreen(isDeviceConnected = true, detectionState = DetectionState.READY)
    }
}

@Preview(name = "3. 실시간 감지 중 - 안정", showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenDetectingStablePreview() {
    AtoCueMobileTheme {
        HomeScreen(
            isDeviceConnected = true,
            detectionState = DetectionState.DETECTING,
            currentStatus = ScratchStatus.STABLE,
            totalScratchSeconds = 0
        )
    }
}

@Preview(name = "3-1. 실시간 감지 중 - 매우위험", showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenDetectingVeryDangerPreview() {
    AtoCueMobileTheme {
        HomeScreen(
            isDeviceConnected = true,
            detectionState = DetectionState.DETECTING,
            currentStatus = ScratchStatus.VERY_DANGER,
            totalScratchSeconds = 120
        )
    }
}

@Preview(name = "4. 일시중지", showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPausedPreview() {
    AtoCueMobileTheme {
        HomeScreen(
            isDeviceConnected = true,
            detectionState = DetectionState.PAUSED
        )
    }
}

@Preview(name = "5. 배터리 부족", showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenBatteryLowPreview() {
    AtoCueMobileTheme {
        HomeScreen(
            isDeviceConnected = true,
            detectionState = DetectionState.BATTERY_LOW
        )
    }
}