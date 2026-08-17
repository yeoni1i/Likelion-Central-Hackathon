package com.example.atocuemobile.ui.screen.timeline.meal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.screen.timeline.ChipBorder
import com.example.atocuemobile.ui.screen.timeline.meal.component.MealRecordCard
import com.example.atocuemobile.ui.screen.timeline.model.MealRecord
import com.example.atocuemobile.ui.screen.timeline.model.MealType
import java.time.LocalDate
import androidx.compose.ui.draw.clip

@Composable
fun MealRecordTab(
    date: LocalDate,
    onAddRecordClick: () -> Unit
) {
    val records = remember {
        MealType.entries.map { type -> MealRecord(date = date, mealType = type, photoUrl = null) }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 24.dp , vertical = 30.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(records) { record ->
            MealRecordCard(record = record, onClick = onAddRecordClick)
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, color = Color(0xFFEBEBEB), RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFFFFF))
                    .clickable { onAddRecordClick() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "새로운 기록 추가",
                        modifier = Modifier.size(62.dp),
                        tint = Color(0xFF6C6E72)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "새로운 기록 추가",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}