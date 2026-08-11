package com.example.atocuemobile.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.R
import com.example.atocuemobile.ui.component.BottomNavTab
import com.example.atocuemobile.ui.component.BottomNavigationBar
import com.example.atocuemobile.ui.theme.AtoCueMobileTheme
import com.example.atocuemobile.ui.theme.Pretendard

private val BaseBackgroundColor = Color(0xFFF6F7FB)
private val SubLabelGray = Color(0xFFADAFB2)
private val DividerColor = Color(0xFFF2F2F4)

@Composable
fun MyPageScreen(
    guardianName: String = "보호자의 이름",
    selectedTab: BottomNavTab = BottomNavTab.MY,
    onTabSelected: (BottomNavTab) -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var showDeviceManagementModal by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = onTabSelected
                )
            },
            containerColor = BaseBackgroundColor
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // 1. 상단 로고 헤더
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 0.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.atocue),
                        contentDescription = "AtoCue Logo",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier.height(15.dp)
                    )
                }

                Spacer(modifier = Modifier.height(35.dp))

                // 2. 프로필 영역 (왼쪽으로 한 번 더 바짝 끌어당김)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 이미지 음수 오프셋을 -28dp로 확대하여 더 좌측 밀착
                    Box(
                        modifier = Modifier
                            .offset(x = (-28).dp) // ⭐ 왼쪽으로 더 밀착
                            .size(130.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.p),
                            contentDescription = "보호자 프로필",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(180.dp)
                        )
                    }

                    // 텍스트도 -24dp 당겨서 프로필 사진에 오붓하게 붙임
                    Column(
                        modifier = Modifier.offset(x = (-24).dp), // ⭐ 텍스트 영역도 왼쪽으로 더 밀착
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "보호자",
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 24.sp,
                                color = SubLabelGray
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = guardianName,
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 27.sp,
                                color = Color(0xFF000000)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. 하단 메뉴 영역 (흰색)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        MyPageMenuItem(
                            title = "기기 관리",
                            onClick = { showDeviceManagementModal = true }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            thickness = 1.dp,
                            color = DividerColor
                        )

                        MyPageMenuItem(
                            title = "로그아웃",
                            onClick = onLogoutClick
                        )
                    }
                }
            }
        }

        // 기기 관리 모달
        if (showDeviceManagementModal) {
            ConnectWatchScreen(
                title = "기기 관리",
                code = "12345",
                onBackClick = { showDeviceManagementModal = false }
            )
        }
    }
}

@Composable
private fun MyPageMenuItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = Pretendard,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 27.sp,
                color = Color(0xFF000000)
            )
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "$title 이동",
            tint = Color(0xFF000000),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyPageScreenPreview() {
    AtoCueMobileTheme {
        MyPageScreen()
    }
}