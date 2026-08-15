package com.example.atocuemobile.ui.screen.timeline.meal.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.atocuemobile.ui.screen.timeline.model.MealRecord
import androidx.compose.ui.draw.clip
import com.example.atocuemobile.ui.screen.timeline.AtoCueBlue
import com.example.atocuemobile.ui.screen.timeline.BackgroundGray
@Composable
fun MealRecordCard(
    record: MealRecord,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp)) // TODO: import androidx.compose.ui.draw.clip
            .background(BackgroundGray)
            .clickable { onClick() },
        contentAlignment = Alignment.BottomStart
    ) {
        if (record.photoUrl == null) {
            Text(
                text = "아직 등록된\n기록이 없습니다",
                modifier = Modifier.align(Alignment.Center)
            )
        }
        // TODO: photoUrl 있으면 Coil AsyncImage로 사진 표시
        // AsyncImage(model = record.photoUrl, contentDescription = null, modifier = Modifier.fillMaxSize())

        Text(
            text = record.mealType.label,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .background(AtoCueBlue, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}