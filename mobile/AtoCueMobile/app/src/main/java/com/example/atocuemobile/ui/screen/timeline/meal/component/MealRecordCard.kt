package com.example.atocuemobile.ui.screen.timeline.meal.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.screen.timeline.AtoCueBlue
import com.example.atocuemobile.ui.screen.timeline.CardBackground
import com.example.atocuemobile.ui.screen.timeline.ChipBorder
import com.example.atocuemobile.ui.screen.timeline.model.MealRecord

@Composable
fun MealRecordCard(
    record: MealRecord,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(146.dp)
            .height(146.dp)
            .clip(RoundedCornerShape(10.15.dp))
            .background(Color(0xFFFAFAFA))
            .border(1.dp, color = Color(0xFFEBEBEB), RoundedCornerShape(10.15.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (record.photoUrl == null) {
            Text(
                text = "아직 등록된\n기록이 없습니다",
                fontSize = 13.sp,
                color = Color(0xFF6C6E72),
                fontWeight = FontWeight(500),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        // TODO: photoUrl 있으면 여기에 Coil AsyncImage로 사진 표시

        Text(
            text = record.mealType.label,
            fontSize = 12.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF2367CE),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 12.dp, end = 12.dp)                 // ← 카드 가장자리로부터 라벨까지 여백
                .background(AtoCueBlue.copy(alpha = 0.35f), RoundedCornerShape(5.dp))  // ← 라벨 배경색 (연한 파랑)
                .padding(horizontal = 10.dp, vertical = 6.dp)         // ← 라벨 내부 텍스트 여백
        )
    }
}