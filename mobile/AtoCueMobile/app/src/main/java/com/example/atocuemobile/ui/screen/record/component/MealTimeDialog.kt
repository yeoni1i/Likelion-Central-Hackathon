package com.example.atocuemobile.ui.screen.record.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class MealTime(val label: String) {
    MORNING("아침"),
    LUNCH("점심"),
    DINNER("저녁"),
    SNACK("간식")
}

@Composable
fun MealTimeDialog(
    selected: MealTime,
    onSelect: (MealTime) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("식사시간") },
        text = {
            Column {
                MealTime.entries.forEach { time ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(time) }
                            .padding(vertical = 12.dp)
                    ) {
                        Text(time.label)
                        if (time == selected) {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("확인") }
        }
    )
}