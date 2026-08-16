package com.example.atocuemobile.ui.screen.record.meal

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun MealCaptureScreen(
    onCaptureComplete: (Bitmap) -> Unit,   // 촬영 완료 -> 다음 화면(기록 입력)으로 사진 전달
    onClose: () -> Unit
) {
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) capturedBitmap = bitmap
    }
    // TODO: 지금은 시스템 기본 카메라 앱을 호출하는 방식(TakePicturePreview)이라
    // 사진1처럼 앱 안에서 실시간 프리뷰 보여주며 촬영 버튼 누르는 커스텀 UI는 아님.
    // 정확히 사진 속 UI(격자선, 안내문구 오버레이)를 원하면 CameraX로 교체 필요 (시간 여유 있을 때)

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // 상단바
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onClose) {
                Text("✕", color = Color.White)  // TODO: Icons.Default.Close 로 교체
            }
            Text("ⓘ", color = Color.White)  // TODO: 정보 아이콘으로 교체
        }

        if (capturedBitmap == null) {
            // ===== 촬영 전 (사진1 왼쪽) =====
            Column(
                modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("아이의 식단을 촬영해주세요!", color = Color.White)
                    Text("기기를 세로로 세워서 정면 이미지를 촬영해주세요", color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(24.dp))
                IconButton(
                    onClick = { cameraLauncher.launch(null) },
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFF5398FF), androidx.compose.foundation.shape.CircleShape)
                ) {
                    Text("📷", color = Color.White) // TODO: Icons.Default.CameraAlt 로 교체
                }
            }
        } else {
            // ===== 촬영 후 (사진1 오른쪽) =====
            Column(modifier = Modifier.fillMaxSize()) {
                Image(
                    bitmap = capturedBitmap!!.asImageBitmap(),
                    contentDescription = "촬영된 식단 사진",
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = { onCaptureComplete(capturedBitmap!!) }) {
                        Text("완료")
                    }
                }
            }
        }
    }
}