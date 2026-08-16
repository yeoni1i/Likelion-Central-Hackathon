package com.example.atocuemobile.ui.screen.record.meal

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun MealCaptureScreen(
    onCaptureComplete: (Bitmap) -> Unit,
    onClose: () -> Unit
) {
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var galleryThumbnail by remember { mutableStateOf<Bitmap?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // TODO: uri -> Bitmap 변환 로직 필요
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "닫기", tint = Color.White)
            }
            Icon(Icons.Default.Info, contentDescription = "정보", tint = Color.White)
        }

        if (capturedBitmap == null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(bottom = 32.dp),
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

                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.DarkGray)
                            .clickable { galleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (galleryThumbnail != null) {
                            Image(
                                bitmap = galleryThumbnail!!.asImageBitmap(),
                                contentDescription = "최근 갤러리 사진",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    IconButton(
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(64.dp)
                            .background(Color(0xFF5398FF), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.PhotoCamera,
                            contentDescription = "촬영",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(48.dp)
                            .background(Color.DarkGray, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Cameraswitch,
                            contentDescription = "카메라 전환",
                            tint = Color.White
                        )
                    }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { capturedBitmap = null }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "다시 찍기",
                            tint = Color.White
                        )
                    }
                    Icon(Icons.Default.Info, contentDescription = "정보", tint = Color.White)
                }

                Image(
                    bitmap = capturedBitmap!!.asImageBitmap(),
                    contentDescription = "촬영된 식단 사진",
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            bitmap = capturedBitmap!!.asImageBitmap(),
                            contentDescription = "촬영된 사진 썸네일",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Button(onClick = { onCaptureComplete(capturedBitmap!!) }) {
                        Text("완료")
                    }
                }
            }
        }
    }
}