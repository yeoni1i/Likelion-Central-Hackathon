package com.example.atocuemobile.ui.screen

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.R
import com.example.atocuemobile.ui.component.PrimaryButton
import com.example.atocuemobile.ui.theme.AtoCueMobileTheme
import com.example.atocuemobile.ui.theme.TitleBlack
import com.example.atocuemobile.ui.theme.DescriptionGray
import com.example.atocuemobile.ui.theme.DividerGray
import com.example.atocuemobile.ui.theme.LabelGray
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.offset

private data class PermissionItem(
    val title: String,
    val description: String,
    val iconRes: Int,
    val iconScale: Float = 1f,
    val iconOffsetX: Dp = 0.dp,
    val textOffsetY: Dp = 0.dp
)

private val permissionItems = listOf(
    PermissionItem("알림", "긁음 경고 알림 메세지 발송", R.drawable.settings),
    PermissionItem("카메라", "식단 촬영", R.drawable.camera),
    PermissionItem("위치", "온도, 습도, 미세먼지 측정", R.drawable.google, iconScale = 2.6f, iconOffsetX = 3.5.dp, textOffsetY = (-6).dp),
    PermissionItem("사진", "식단 이미지 첨부", R.drawable.gallery)
)

@Composable
fun PermissionScreen(
    onConfirm: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            IconButton(onClick = onNavigateBack, modifier = Modifier.padding(4.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
            }

            Spacer(modifier = Modifier.height(16.dp)) // 전체를 살짝 아래로

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "앱 사용을 위해\n접근 권한을 허용해주세요",
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        lineHeight = 33.sp,
                        color = TitleBlack
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "선택권한",
                    style = TextStyle(fontSize = 14.sp, color = LabelGray)
                )

                Spacer(modifier = Modifier.height(8.dp))

                permissionItems.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Icon(
                            painter = painterResource(item.iconRes),
                            contentDescription = item.title,
                            modifier = Modifier
                                .size(52.dp)
                                .scale(item.iconScale)
                                .offset(x = item.iconOffsetX)
                                .padding(end = 12.dp),
                            tint = Color.Unspecified
                        )
                        Text(
                            text = item.title,
                            style = TextStyle(fontSize = 16.sp, color = TitleBlack),
                            modifier = Modifier
                                .width(56.dp)
                                .offset(y = item.textOffsetY)
                        )
                        Text(
                            text = item.description,
                            style = TextStyle(fontSize = 15.sp, color = DescriptionGray),
                            modifier = Modifier.offset(y = item.textOffsetY)
                        )
                    }
                }

                HorizontalDivider(
                    color = DividerGray,
                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
                )

                Text(
                    text = "선택권한의 경우 허용하지않아도 서비스를 사용할 수 있으나 일부 서비스 이용이 제한될 수 있습니다.",
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = LabelGray
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            PrimaryButton(
                text = "확인",
                onClick = onConfirm
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PermissionScreenPreview() {
    AtoCueMobileTheme {
        PermissionScreen(
            onConfirm = {},
            onNavigateBack = {}
        )
    }
}