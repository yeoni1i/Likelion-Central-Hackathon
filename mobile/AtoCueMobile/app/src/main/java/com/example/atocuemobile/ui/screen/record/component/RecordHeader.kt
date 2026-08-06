package com.example.atocuemobile.ui.screen.record.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun RecordHeader(
    title: String,          // 예: "식단 기록", "생활 기록"
    dateLabel: String,      // 예: "0월 00일 (요일), 오늘"
    onBackClick: () -> Unit,
    onDateClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
            }
            Text(text = title)
            // TODO: 우측 캐릭터 아이콘(고양이 등) 있으면 여기에 Image 추가
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = dateLabel)
            IconButton(onClick = onDateClick) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "날짜 선택")
            }
        }
    }
}