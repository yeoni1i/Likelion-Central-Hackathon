package com.example.scratchdetecter.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Text
import com.example.scratchdetecter.presentation.component.MascotCharacter

@Composable
fun BatteryLowScreen(onPause: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 슬픈 표정 이미지가 준비되면 MascotCharacter 내부 리소스만 교체한다.
        MascotCharacter(message = "배터리가 부족해", imageSizeDp = 128)
        Spacer(Modifier.height(0.dp))
        Button(
            onClick = onPause,
            modifier = Modifier.width(142.dp).height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF514E50),
                contentColor = Color.White
            )
        ) {
            Text("일시 중지", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}
