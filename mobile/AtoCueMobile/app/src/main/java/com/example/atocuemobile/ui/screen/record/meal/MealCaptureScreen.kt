package com.example.atocuemobile.ui.screen.record.meal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.R

@Composable
fun MealCaptureScreen(
    onCapturedComplete: () -> Unit,
    onBack: () -> Unit
) {
    var isCaptured by remember { mutableStateOf(false) }
    // 🌟 인포 박스 표시 여부 상태 (기본값: false - 숨김 처리)
    var showInfoBox by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        // 1. 카메라 프리뷰 박스 (사진 영역)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.78f)
                .align(Alignment.TopCenter)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "식단 촬영 프리뷰",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 🌟 안내 가이드 박스: showInfoBox가 true이고 아직 촬영 전(!isCaptured)일 때만 표시
            AnimatedVisibility(
                visible = showInfoBox && !isCaptured,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0x99000000), RoundedCornerShape(12.dp)) // 투명 검은색 배경
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "아이의 식단을 촬영해주세요!",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "기기를 세로로 세워서 정면 이미지를 촬영해주세요",
                            color = Color(0xFFDDDDDD),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 2. 상단 바 (뒤로가기/닫기, 정보 아이콘)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = if (isCaptured) Icons.Default.ArrowBack else Icons.Default.Close,
                    contentDescription = "닫기",
                    tint = Color.White
                )
            }
            // 🌟 인포 아이콘 클릭 시 showInfoBox 토글 (켜기/끄기)
            IconButton(onClick = { showInfoBox = !showInfoBox }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "정보",
                    tint = if (showInfoBox) Color(0xFF4A90E2) else Color.White // 활성화 상태일 때 색상 강조
                )
            }
        }

        // 3. 하단 컨트롤 영역 (보내주신 코드 스타일 100% 유지)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 좌측 갤러리 프리뷰 썸네일 박스
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Gray)
            )

            if (!isCaptured) {
                // 중앙 파란색 촬영 버튼 (카메라 아이콘 + 원형 테두리 감싸기)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color(0xFF4A90E2).copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { isCaptured = true },
                        modifier = Modifier
                            .size(58.dp)
                            .background(Color(0xFF4A90E2), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "촬영하기",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // 우측 다시 찍기 버튼 (네모 형태 배경)
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color(0xFF333333), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "재촬영",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier.height(72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onCapturedComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A4A4A)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text("완료", color = Color.White, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}