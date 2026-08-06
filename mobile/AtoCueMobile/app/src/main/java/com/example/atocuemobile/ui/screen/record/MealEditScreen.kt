package com.example.atocuemobile.ui.screen.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.atocuemobile.ui.screen.record.component.FoodInputRow
import com.example.atocuemobile.ui.screen.record.component.MealTime
import com.example.atocuemobile.ui.screen.record.component.MealTimeDialog
import com.example.atocuemobile.ui.screen.record.component.RecordDatePickerDialog
import com.example.atocuemobile.ui.screen.record.component.RecordHeader

@Composable
fun MealEditScreen(
    photoUri: String,  // TODO: 촬영 화면에서 넘겨받은 사진, AsyncImage/Image로 표시
    onBackClick: () -> Unit,
    onSubmit: (mealTime: MealTime, foodList: List<String>) -> Unit
) {
    var showMealTimeDialog by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var selectedMealTime by remember { mutableStateOf(MealTime.MORNING) }
    var foodList by remember { mutableStateOf(listOf("", "")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        RecordHeader(
            title = "식단 기록",
            dateLabel = "0월 00일 (요일), 오늘",
            onBackClick = onBackClick,
            onDateClick = { showDatePickerDialog = true }
        )

        // TODO: photoUri로 실제 이미지 표시 (Coil 등 이미지 라이브러리 사용 권장)

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("먹은음식", modifier = Modifier.weight(1f))
            AssistChip(
                onClick = { showMealTimeDialog = true },
                label = { Text(selectedMealTime.label) }
            )
        }

        foodList.forEachIndexed { index, food ->
            FoodInputRow(
                value = food,
                onValueChange = { newValue ->
                    foodList = foodList.toMutableList().also { it[index] = newValue }
                },
                onRemoveClick = {
                    foodList = foodList.toMutableList().also { it.removeAt(index) }
                }
            )
        }

        Button(onClick = { foodList = foodList + "" }) {
            Text("+ 메뉴 추가")
        }

        Button(
            onClick = { onSubmit(selectedMealTime, foodList) },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("등록하기")
        }
    }

    if (showMealTimeDialog) {
        MealTimeDialog(
            selected = selectedMealTime,
            onSelect = { selectedMealTime = it },
            onConfirm = { showMealTimeDialog = false },
            onDismiss = { showMealTimeDialog = false }
        )
    }

    if (showDatePickerDialog) {
        RecordDatePickerDialog(
            onConfirm = { showDatePickerDialog = false /* TODO: 선택 날짜 반영 */ },
            onDismiss = { showDatePickerDialog = false }
        )
    }
}