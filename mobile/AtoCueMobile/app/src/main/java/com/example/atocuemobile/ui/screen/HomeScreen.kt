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
import androidx.compose.material3.HorizontalDivider
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
import com.example.atocuemobile.viewmodel.TimelineUiItem

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
private val TimelineDividerColor = Color(0xFFE5E7EB)

private val DisconnectedChipBg = Color(0xFFD0D6DD)
private val DisconnectedChipDot = Color(0xFF6C6E72)
private val ConnectedChipBg = Color(0x265398FF)
private val ConnectedBlueColor = Color(0xFF397FE9)

private val ActiveDotColor = Color(0xFF5398FF)
private val InactiveDotColor = Color(0xFFD9D9D9)

enum class DetectionState {
    NOT_CONNECTED,
    READY,
    DETECTING,
    PAUSED,
    BATTERY_LOW
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
    pairingCode: String = "123456",
    currentStatus: ScratchStatus = ScratchStatus.STABLE,
    totalScratchSeconds: Int = 0,
    timeCriteriaText: String = "00월 00일 (수) 00시 기준",
    weatherData: WeatherResponse? = null,
    guideList: List<GuideMessage> = listOf(
        GuideMessage("실내습도 00%이상 유지", "오늘은 지난 3일보다 평균 기온이 높고 습도가 낮아요 실내에서는 적정한 온도와 높은 습도를 유지해주세요"),
        GuideMessage(
            title = "보습제 자주 덧바르기",
            description = "외출 전후와 건조함을 느낄 때마다 보습제를 꼼꼼히 덧발라 피부 장벽을 보호해주세요"
        ),
        GuideMessage(
            title = "미세먼지 차단 및 환기",
            description = "공기 질 상태에 맞춰 적절한 환기를 진행하고 외출 시 마스크 착용을 권장합니다"
        )
    ),

    timelineItems: List<TimelineUiItem> = emptyList(),
    onConnectWatchClick: () -> Unit = {},
    onStartDetectionClick: () -> Unit = {},
    onStopDetectionClick: () -> Unit = {},
    onRestartDetectionClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onLifeLogClick: () -> Unit = {},
    onMealLogClick: () -> Unit = {},
    selectedTab: BottomNavTab = BottomNavTab.HOME,
    onTabSelected: (BottomNavTab) -> Unit = {}
) {
    var showConnectModal by remember { mutableStateOf(false) }
    var isFabExpanded by remember { mutableStateOf(false) }

    val fabRotation by animateFloatAsState(
        targetValue = if (isFabExpanded) 45f else 0f,
        label = "fabRotation"
    )

    val effectiveConnected = isDeviceConnected && (detectionState != DetectionState.NOT_CONNECTED && detectionState != DetectionState.BATTERY_LOW)

    val tempText = weatherData?.let { "${it.temperature.toInt()}도" } ?: "--도"
    val humidityText = weatherData?.let { "${it.humidity}%" } ?: "--%"
    val airQualityText = weatherData?.airQuality ?: "--"

    val fineDustIconRes = when (airQualityText) {
        "매우 좋음", "매우좋음" -> R.drawable.one
        "좋음" -> R.drawable.two
        "보통" -> R.drawable.three
        "나쁨" -> R.drawable.four
        "매우 나쁨", "매우나쁨" -> R.drawable.five
        else -> R.drawable.one
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
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

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

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Surface(
                                        color = currentStatus.badgeBgColor,
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier
                                            .width(86.dp)
                                            .height(36.dp)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = currentStatus.badgeLabel,
                                                style = TextStyle(
                                                    fontFamily = Pretendard,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp,
                                                    color = currentStatus.badgeTextColor,
                                                    textAlign = TextAlign.Center
                                                )
                                            )
                                        }
                                    }
                                }

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

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isDeviceConnected && timelineItems.isNotEmpty()) {
                            val groupedItems = timelineItems.groupBy { it.hourLabel }
                            groupedItems.forEach { (hour, items) ->
                                TimelineSectionGroup(time = hour) {
                                    items.forEach { item ->
                                        TimelineCard(
                                            status = item.status.badgeLabel,
                                            statusColor = item.status.badgeTextColor,
                                            iconRes = item.status.timelineImageRes,
                                            timeRange = item.timeRangeLabel,
                                            duration = item.durationLabel
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        } else if (isDeviceConnected) {
                            TimelineSectionGroup(time = "11:00\nAM") {
                                TimelineCard(status = "안정", statusColor = Color(0xFF22C55E), iconRes = R.drawable.verygood, timeRange = "00:00~00:00", duration = "00분")
                                TimelineCard(status = "보통", statusColor = Color(0xFF3B82F6), iconRes = R.drawable.good, timeRange = "00:00~00:00", duration = "00분")
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            TimelineSectionGroup(time = "10:00\nAM") {
                                TimelineCard(status = "주의", statusColor = Color(0xFFF59E0B), iconRes = R.drawable.bad, timeRange = "00:00~00:00", duration = "00분")
                                TimelineCard(status = "보통", statusColor = Color(0xFF3B82F6), iconRes = R.drawable.good, timeRange = "00:00~00:00", duration = "00분")
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            TimelineSectionGroup(time = "09:00\nAM") {
                                TimelineCard(status = "안정", statusColor = Color(0xFF22C55E), iconRes = R.drawable.verygood, timeRange = "00:00~00:00", duration = "00분")
                                TimelineCard(status = "위험", statusColor = Color(0xFFEF4444), iconRes = R.drawable.verybad, timeRange = "00:00~00:00", duration = "00분")
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

            AnimatedVisibility(
                visible = isFabExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x80000000))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isFabExpanded = false
                        }
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AnimatedVisibility(
                    visible = isFabExpanded,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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
                code = pairingCode,
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
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = time,
                style = TextStyle(
                    fontFamily = Pretendard,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF6C6E72),
                    textAlign = TextAlign.Right
                ),
                modifier = Modifier.width(45.dp)
            )

            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = TimelineDividerColor
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.width(45.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun TimelineCard(
    status: String,
    statusColor: Color,
    iconRes: Int,
    timeRange: String,
    duration: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color(0x08000000),
                ambientColor = Color(0x08000000)
            )
            .background(Color.White, RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = status,
                        modifier = Modifier.size(22.dp)
                    )

                    Text(
                        text = status,
                        style = TextStyle(
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.5.sp,
                            color = statusColor
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = duration,
                        style = TextStyle(
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = Color(0xFF121212)
                        )
                    )
                    Text(
                        text = "지속",
                        style = TextStyle(
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            color = Color(0xFF6C6E72)
                        ),
                        modifier = Modifier.padding(bottom = 1.dp)
                    )
                }
            }

            Text(
                text = "발생시각 | $timeRange",
                style = TextStyle(
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = Color(0xFF9E9E9E)
                )
            )
        }
    }
}

// ==========================================
// Preview 모음
// ==========================================

@Preview(name = "1. 워치 연결 전", group = "상태별", showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreenNotConnected() {
    AtoCueMobileTheme {
        HomeScreen(
            isDeviceConnected = false,
            detectionState = DetectionState.NOT_CONNECTED
        )
    }
}

@Preview(name = "2. 감지 시작 전 (READY)", group = "상태별", showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreenReady() {
    AtoCueMobileTheme {
        HomeScreen(
            isDeviceConnected = true,
            detectionState = DetectionState.READY
        )
    }
}

@Preview(name = "3-1. 감지 중 - 안정 (STABLE)", group = "감지 단계", showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreenDetectingStable() {
    AtoCueMobileTheme {
        HomeScreen(
            isDeviceConnected = true,
            detectionState = DetectionState.DETECTING,
            currentStatus = ScratchStatus.STABLE
        )
    }
}

@Preview(name = "3-2. 감지 중 - 보통 (NORMAL)", group = "감지 단계", showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreenDetectingNormal() {
    AtoCueMobileTheme {
        HomeScreen(
            isDeviceConnected = true,
            detectionState = DetectionState.DETECTING,
            currentStatus = ScratchStatus.NORMAL
        )
    }
}

@Preview(name = "3-3. 감지 중 - 경고 (WARNING)", group = "감지 단계", showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreenDetectingWarning() {
    AtoCueMobileTheme {
        HomeScreen(
            isDeviceConnected = true,
            detectionState = DetectionState.DETECTING,
            currentStatus = ScratchStatus.WARNING
        )
    }
}

@Preview(name = "3-4. 감지 중 - 위험 (DANGER)", group = "감지 단계", showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreenDetectingDanger() {
    AtoCueMobileTheme {
        HomeScreen(
            isDeviceConnected = true,
            detectionState = DetectionState.DETECTING,
            currentStatus = ScratchStatus.DANGER
        )
    }
}

@Preview(name = "3-5. 감지 중 - 매우위험 (VERY_DANGER)", group = "감지 단계", showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreenDetectingVeryDanger() {
    AtoCueMobileTheme {
        HomeScreen(
            isDeviceConnected = true,
            detectionState = DetectionState.DETECTING,
            currentStatus = ScratchStatus.VERY_DANGER
        )
    }
}

@Preview(name = "4. 감지 일시중지 (PAUSED)", group = "상태별", showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreenPaused() {
    AtoCueMobileTheme {
        HomeScreen(
            isDeviceConnected = true,
            detectionState = DetectionState.PAUSED
        )
    }
}

@Preview(name = "5. 배터리 부족 (BATTERY_LOW)", group = "상태별", showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreenBatteryLow() {
    AtoCueMobileTheme {
        HomeScreen(
            isDeviceConnected = true,
            detectionState = DetectionState.BATTERY_LOW
        )
    }
}