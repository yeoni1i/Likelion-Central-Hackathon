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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.atocuemobile.ui.screen.timeline.AtoCueBlue
import com.example.atocuemobile.ui.screen.timeline.model.MealRecord

@Composable
fun MealRecordCard(
    record: MealRecord,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // 1:1 정사각형 비율 유지
            .clip(RoundedCornerShape(10.15.dp))
            .background(Color(0xFFFAFAFA))
            .border(1.dp, color = Color(0xFFEBEBEB), shape = RoundedCornerShape(10.15.dp))
            .clickable { onClick() } // 카드를 누르면 입력/수정 화면으로 이동
    ) {
        // 1. 등록된 사진이 있는 경우: 카드 전체에 사진을 꽉 채워 렌더링
        if (!record.photoUrl.isNullOrBlank()) {
            AsyncImage(
                model = record.photoUrl,
                contentDescription = "식단 사진",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // 2. 등록된 사진이 없을 때 "아직 등록된 기록이 없습니다" 텍스트 표시
            Text(
                text = "아직 등록된\n기록이 없습니다",
                fontSize = 13.sp,
                color = Color(0xFF6C6E72),
                fontWeight = FontWeight(500),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 19.dp)
            )
        }

        // 3. 우측 하단 MealType 뱃지 (아침식사, 점심식사, 저녁식사, 간식) - 항상 유지!
        Text(
            text = record.mealType.label,
            fontSize = 12.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF2367CE),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 12.dp, end = 12.dp)
                .background(
                    color = AtoCueBlue.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}