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
    guardianName: String = "보호자",
    pairingCode: String = "123456",
    selectedTab: BottomNavTab = BottomNavTab.MY,
    onTabSelected: (BottomNavTab) -> Unit = {},
    onDeviceManageClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
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
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            // 1. 상단 로고 헤더
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.atocue),
                    contentDescription = "AtoCue Logo",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.height(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. 프로필 영역
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .offset(x = (-28).dp)
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

                Column(
                    modifier = Modifier.offset(x = (-20).dp),
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

                    Spacer(modifier = Modifier.height(2.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

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
                        onClick = onDeviceManageClick
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
        MyPageScreen(guardianName = "홍길동")
    }
}