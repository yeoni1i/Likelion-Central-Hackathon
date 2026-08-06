package com.example.atocuemobile.ui.screen.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LifeRecordScreen(
    onSubmit: (symptom: String, careNote: String) -> Unit
) {
    var symptom by remember { mutableStateOf("") }
    var careNote by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("생활 기록")

        // 주요 증상 드롭다운 (지금은 텍스트 필드로 임시 구현, 실제 옵션 리스트 나오면 ExposedDropdownMenu로 교체)
        OutlinedTextField(
            value = symptom,
            onValueChange = { symptom = it },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            label = { Text("주요 증상") }
        )

        OutlinedTextField(
            value = careNote,
            onValueChange = { careNote = it },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            label = { Text("특이사항 기록") }
        )

        Button(
            onClick = { onSubmit(symptom, careNote) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .height(48.dp)
        ) {
            Text("등록하기")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LifeRecordScreenPreview() {
    LifeRecordScreen(onSubmit = { _, _ -> })
}