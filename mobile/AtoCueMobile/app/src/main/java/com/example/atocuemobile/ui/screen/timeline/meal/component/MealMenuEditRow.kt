package com.example.atocuemobile.ui.screen.timeline.meal.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MealMenuEditRow(menuName: String) {
    // TODO: 수정 모드일 때 삭제 버튼(X)이나 텍스트 입력 필드로 바뀌도록 확장 필요
    Text(
        text = menuName,
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    )
}