package com.example.atocuemobile.ui.screen.timeline.meal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.atocuemobile.ui.screen.timeline.meal.component.MealRecordCard
import com.example.atocuemobile.ui.screen.timeline.model.MealRecord
import com.example.atocuemobile.ui.screen.timeline.model.MealType
import java.time.LocalDate
import androidx.compose.foundation.clickable

@Composable
fun MealRecordTab(
    date: LocalDate,
    onAddRecordClick: () -> Unit
) {
    // TODO: 기본 4개(아침/점심/저녁/간식) + 추가로 등록한 기록들을 ViewModel에서 받아오도록 교체
    val records = remember {
        MealType.entries.map { type -> MealRecord(date = date, mealType = type, photoUrl = null) }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(records) { record ->
            MealRecordCard(record = record, onClick = onAddRecordClick)
            // TODO: 이미 기록이 있는 카드를 누르면 MealRecordDetailScreen으로,
            // 비어있는 카드를 누르면 기록 추가 화면으로 분기해야 함 (지금은 둘 다 onAddRecordClick)
        }

        item {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable { onAddRecordClick() }, // TODO: import androidx.compose.foundation.clickable
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "새로운 기록 추가")
                    Text(text = "새로운 기록 추가")
                }
            }
        }
    }
}