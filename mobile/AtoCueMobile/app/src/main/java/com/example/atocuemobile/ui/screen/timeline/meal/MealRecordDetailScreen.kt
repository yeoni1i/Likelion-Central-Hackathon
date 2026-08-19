package com.example.atocuemobile.ui.screen.timeline.meal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.atocuemobile.ui.screen.timeline.meal.component.MealMenuEditRow
import com.example.atocuemobile.ui.screen.timeline.model.MealRecord
import androidx.compose.foundation.clickable

// 두번째 사진의 우측 화면 - 식단 기록 상세/수정
@Composable
fun MealRecordDetailScreen(
    record: MealRecord,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
            }
            Text(text = "식단기록")
            // TODO: 제목 가운데 정렬 or Row weight 조정
        }

        Text(
            text = "0월 00일 (요일), 오늘", // TODO: record.date 기반으로 실제 포맷팅
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // TODO: record.photoUrl 있으면 Coil AsyncImage로 사진 표시, 없으면 placeholder
        Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
            Text(text = "[사진 영역]", modifier = Modifier.align(Alignment.Center))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = record.mealType.label)
            Text(
                text = "수정하기",
                modifier = Modifier.clickable { onEditClick() }
                // TODO: import androidx.compose.foundation.clickable
            )
        }

        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(record.menuItems) { menu -> MealMenuEditRow(menu) }
        }
    }
}