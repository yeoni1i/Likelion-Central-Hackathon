package com.example.atocuemobile.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
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
import com.example.atocuemobile.R
import com.example.atocuemobile.network.dto.WeatherResponse
import com.example.atocuemobile.ui.component.BottomNavTab
import com.example.atocuemobile.ui.component.BottomNavigationBar
import com.example.atocuemobile.ui.theme.AtoCueMobileTheme
import com.example.atocuemobile.ui.theme.LabelGray
import com.example.atocuemobile.ui.theme.Pretendard
import com.example.atocuemobile.ui.theme.PrimaryBlue

private val BaseBackgroundColor = Color(0xFFF6F7FB)
private val ChipBgColor = Color(0xFFD0D6DD)
private val ChipDotColor = Color(0xFF6C6E72)
private val WatchBtnBgColor = Color(0xFF36383D)
private val TitleBlackColor = Color(0xFF121212)
private val WeatherCardBorderColor = Color(0xFFF3F4F6)
private val InfoBoxBgColor = Color(0xFFF2F4F7)
private val LightTextGray = Color(0xFF9E9E9E)

private val ActiveDotColor = Color(0xFF5398FF)
private val InactiveDotColor = Color(0xFFD9D9D9)

data class GuideMessage(
    val title: String,
    val description: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    weatherData: WeatherResponse? = null,
    guideList: List<GuideMessage> = listOf(
        GuideMessage("가이드 제목 1", "추후 백엔드 API 연동으로 맞춤 메시지가 표시되는 공간입니다."),
        GuideMessage("가이드 제목 2", "추후 백엔드 API 연동으로 맞춤 메시지가 표시되는 공간입니다."),
        GuideMessage("가이드 제목 3", "추후 백엔드 API 연동으로 맞춤 메시지가 표시되는 공간입니다.")
    ),
    onConnectWatchClick: () -> Unit = {},
    selectedTab: BottomNavTab = BottomNavTab.HOME,
    onTabSelected: (BottomNavTab) -> Unit = {}
) {
    var showConnectModal by remember { mutableStateOf(false) }

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
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.atocue),
                        contentDescription = "AtoCue Logo",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier.height(15.dp)
                    )

                    Surface(
                        color = ChipBgColor,
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(ChipDotColor, CircleShape)
                            )
                            Text(
                                text = "기기 연결 없음",
                                style = TextStyle(
                                    fontFamily = Pretendard,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TitleBlackColor
                                )
                            )
                        }
                    }
                }

                // 워치 연결 히어로 영역
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 110.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "워치를 연결해주세요",
                        style = TextStyle(
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = TitleBlackColor
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "연결된 워치가 없습니다.\n연결상태를 확인해주세요",
                        style = TextStyle(
                            fontFamily = Pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = TitleBlackColor,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            showConnectModal = true
                            onConnectWatchClick()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WatchBtnBgColor),
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

                // 오늘의 날씨 섹션
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
                            .padding(horizontal = 24.dp, vertical = 36.dp)
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

                        Spacer(modifier = Modifier.height(20.dp))

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

                        Spacer(modifier = Modifier.height(20.dp))

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

                // 오늘의 경고 타임라인 섹션
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

                        Spacer(modifier = Modifier.height(28.dp))

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

            // 플로팅 '+' 버튼
            FloatingActionButton(
                onClick = { },
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 16.dp)
                    .size(52.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "기록 추가")
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
            .padding(top = 22.dp, bottom = 18.dp, start = 4.dp, end = 4.dp),
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    AtoCueMobileTheme {
        HomeScreen(
            weatherData = WeatherResponse(
                temperature = 24.0,
                humidity = 55,
                airQuality = "매우 좋음"
            )
        )
    }
}