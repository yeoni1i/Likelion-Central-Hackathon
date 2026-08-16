package com.example.atocuemobile.ui.screen.record.meal.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.atocuemobile.ui.screen.timeline.model.MealType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealTimeSelectSheet(
    selectedType: MealType,
    onSelect: (MealType) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "식사시간")
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "닫기")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            MealType.entries.forEach { type ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(type) }
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = mealTimeLabel(type))
                    if (type == selectedType) {
                        Icon(Icons.Default.Check, contentDescription = "선택됨")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(text = "확인")
            }
        }
    }
}

// 사진 보면 "아침식사"가 아니라 "아침"으로 짧게 표시되어 있어서 별도 라벨 매핑
private fun mealTimeLabel(type: MealType): String = when (type) {
    MealType.BREAKFAST -> "아침"
    MealType.LUNCH -> "점심"
    MealType.DINNER -> "저녁"
    MealType.SNACK -> "간식"
}