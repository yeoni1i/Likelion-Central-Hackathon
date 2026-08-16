package com.example.atocuemobile.ui.screen.record.meal

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.atocuemobile.ui.screen.record.component.RecordDatePickerDialog
import com.example.atocuemobile.ui.screen.record.meal.component.MealTimeSelectSheet
import com.example.atocuemobile.ui.screen.record.meal.component.MenuInputRow
import com.example.atocuemobile.ui.screen.timeline.model.MealType
import java.time.LocalDate
import java.time.YearMonth
import com.example.atocuemobile.ui.screen.timeline.model.MealType



@Composable
fun MealRecordInputScreen(
    capturedPhoto: Bitmap?,
    onBackClick: () -> Unit,
    onRegisterComplete: (mealType: MealType, date: LocalDate, menuItems: List<String>) -> Unit
    // TODO: 실제 저장(API 호출)은 이 콜백에서 처리하도록 상위(ViewModel)에 연결
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var displayedMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    var showDatePicker by remember { mutableStateOf(false) }

    var mealType by remember { mutableStateOf(MealType.BREAKFAST) }
    var showMealTimeSheet by remember { mutableStateOf(false) }

    val menuItems = remember { mutableStateListOf("") }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
            }
            Text(text = "식단 기록")

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { showDatePicker = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = formatDateLabel(selectedDate))
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "날짜 선택")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 사진 영역
        if (capturedPhoto != null) {
            Image(
                bitmap = capturedPhoto.asImageBitmap(),
                contentDescription = "촬영된 식단 사진",
                modifier = Modifier.fillMaxWidth().height(220.dp)
            )
        } else {
            Box(
                modifier = Modifier.fillMaxWidth().height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "[사진 없음]")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "먹은음식")
                AssistChip(
                    onClick = { showMealTimeSheet = true },
                    label = { Text(text = mealTimeLabel(mealType)) },
                    trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) }
                    // TODO: 색상/모양은 사진2 파란 알약 버튼 스타일로 커스텀 필요
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            menuItems.forEachIndexed { index, item ->
                MenuInputRow(
                    value = item,
                    onValueChange = { newValue -> menuItems[index] = newValue },
                    onRemoveClick = { menuItems.removeAt(index) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { menuItems.add("") }
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "메뉴 추가")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "메뉴 추가")
            }
        }

        Button(
            onClick = {
                onRegisterComplete(mealType, selectedDate, menuItems.filter { it.isNotBlank() })
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp)
        ) {
            Text(text = "등록하기")
        }
    }

    if (showMealTimeSheet) {
        MealTimeSelectSheet(
            selectedType = mealType,
            onSelect = { mealType = it },
            onConfirm = { showMealTimeSheet = false },
            onDismiss = { showMealTimeSheet = false }
        )
    }

    if (showDatePicker) {
        RecordDatePickerDialog(
            yearMonth = displayedMonth,
            selectedDate = selectedDate,
            onPrevMonth = { displayedMonth = displayedMonth.minusMonths(1) },
            onNextMonth = { displayedMonth = displayedMonth.plusMonths(1) },
            onDateSelect = { selectedDate = it },
            onConfirm = { showDatePicker = false },
            onDismiss = { showDatePicker = false }
        )
    }
}

private fun mealTimeLabel(type: MealType): String = when (type) {
    MealType.BREAKFAST -> "아침"
    MealType.LUNCH -> "점심"
    MealType.DINNER -> "저녁"
    MealType.SNACK -> "간식"
}

private fun formatDateLabel(date: LocalDate): String {
    val today = LocalDate.now()
    val suffix = if (date == today) ", 오늘" else ""
    val dayOfWeekKor = listOf("월", "화", "수", "목", "금", "토", "일")[date.dayOfWeek.value - 1]
    return "${date.monthValue}월 ${date.dayOfMonth}일 (${dayOfWeekKor}요일)$suffix"
}