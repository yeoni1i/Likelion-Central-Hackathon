package com.example.atocuemobile.ui.screen.timeline.scratch.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.screen.timeline.model.ScratchEvent

@Composable
fun ScratchEventItem(event: ScratchEvent) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .border(
                width = 1.dp,
                color = Color(0xFFF0F0F0),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top // 🌟 카드의 맨 위(첫 번째 줄) 높이에 맞춰 정렬!
        ) {
            // 1. 왼쪽 영역 (이모티콘+상태 텍스트 & 발생시각)
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                // [첫 번째 줄] 이모티콘 + 상태 텍스트
                Row(
                    modifier = Modifier.height(28.dp), // ★ 첫 번째 줄 높이 고정 (28dp)
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = event.level.iconRes),
                        contentDescription = event.level.label,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = event.level.label,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = event.level.color
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // [두 번째 줄] 발생시각
                Text(
                    text = "발생시각 | ${event.startTime}~${event.endTime}",
                    fontSize = 12.sp,
                    color = Color(0xFF8E8E93)
                )
            }

            // 2. 오른쪽 영역: '00분 지속' (상태 텍스트와 동일한 높이의 첫 줄에 위치!)
            Row(
                modifier = Modifier.height(28.dp), // ★ 상태 텍스트 줄과 똑같은 28dp 높이 부여
                verticalAlignment = Alignment.Bottom // 글자의 아래 기준선 맞춤
            ) {
                Text(
                    text = "${event.durationMinutes}분",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "지속",
                    fontSize = 12.sp,
                    color = Color(0xFF8E8E93),
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}