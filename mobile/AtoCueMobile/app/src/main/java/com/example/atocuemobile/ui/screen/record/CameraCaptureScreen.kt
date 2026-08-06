package com.example.atocuemobile.ui.screen.record

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CameraCaptureScreen(
    onClose: () -> Unit,
    onCaptureComplete: (photoUri: String) -> Unit
    // TODO: 실제 카메라 연동 시 CameraX 관련 파라미터 추가 필요
) {
    // 촬영 전/후 상태 구분 (사진 1, 2)
    var isCaptured by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 상단 바
        Row(modifier = Modifier.fillMaxWidth()) {
            if (!isCaptured) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "닫기")
                }
            } else {
                IconButton(onClick = { isCaptured = false }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                }
            }
            // TODO: 오른쪽 정렬로 info 아이콘 배치
            IconButton(onClick = { /* TODO: 안내 다이얼로그 */ }) {
                Icon(Icons.Default.Info, contentDescription = "안내")
            }
        }

        // TODO: 카메라 미리보기 or 촬영된 이미지. CameraX PreviewView 또는 Image로 교체
        Column(modifier = Modifier.fillMaxSize()) {
            if (!isCaptured) {
                Text("아이의 식단을 촬영해주세요!")
                Text("기기를 세로로 세워서 정면 이미지를 촬영해주세요")
            }
        }

        // 하단 컨트롤 바
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // TODO: 좌측 갤러리 썸네일 (최근 촬영 이미지)
            Box {}

            if (!isCaptured) {
                IconButton(onClick = { isCaptured = true /* TODO: 실제 촬영 로직 */ }) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "촬영")
                }
                IconButton(onClick = { /* TODO: 전/후면 카메라 전환 */ }) {
                    Icon(Icons.Default.Cameraswitch, contentDescription = "카메라 전환")
                }
            } else {
                Button(onClick = { onCaptureComplete("") /* TODO: 실제 사진 URI 전달 */ }) {
                    Text("완료")
                }
            }
        }
    }
}