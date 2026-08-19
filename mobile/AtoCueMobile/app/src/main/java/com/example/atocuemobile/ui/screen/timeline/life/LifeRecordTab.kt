package com.example.atocuemobile.ui.screen.timeline.life

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.screen.timeline.ChipBorder
import com.example.atocuemobile.ui.screen.timeline.MainBackGroundColor
import com.example.atocuemobile.ui.screen.timeline.life.component.ShowerMoisturizerSection
import com.example.atocuemobile.ui.screen.timeline.life.component.SymptomSection
import com.example.atocuemobile.ui.screen.timeline.model.LifeRecord
import java.time.LocalDate

@Composable
fun LifeRecordTab(
    date: LocalDate,
    onNavigateToLifeRecordInput: () -> Unit
) {
    var record by remember { mutableStateOf(LifeRecord()) }
    var hasRecord by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    // 🌟 기록이 없고, 수정(입력) 중도 아닐 때만 Empty 화면 표시
    if (!hasRecord && !isEditing) {
        EmptyLifeRecord(
            onStartInputClick = {
                isEditing = true // 🌟 버튼 누르면 바로 입력(편집) 모드로 전환!
                onNavigateToLifeRecordInput()
            }
        )
        return
    }

    // 🌟 입력(수정) 모드이거나 기록이 이미 있을 때 보여지는 화면
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackGroundColor)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = if (isEditing) "완료" else "수정하기",
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    if (isEditing) {
                        hasRecord = true
                        isEditing = false
                    } else {
                        isEditing = true
                        onNavigateToLifeRecordInput()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ShowerMoisturizerSection(
            showerCount = record.showerCount,
            moisturizerCount = record.moisturizerCount,
            isEditMode = isEditing,
            onShowerCountChange = { record = record.copy(showerCount = it) },
            onMoisturizerCountChange = { record = record.copy(moisturizerCount = it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        SymptomSection(
            selectedSymptoms = record.symptoms,
            isEditMode = isEditing,
            onSymptomToggle = { symptom ->
                val updated = if (symptom in record.symptoms) {
                    record.symptoms - symptom
                } else {
                    record.symptoms + symptom
                }
                record = record.copy(symptoms = updated)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (!isEditing && record.note.isNotBlank()) {
            Text(
                text = "특이사항 기록",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, ChipBorder, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = record.note,
                    fontSize = 14.sp,
                    color = Color.Black,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyLifeRecord(
    onStartInputClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackGroundColor)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "생활기록이 비어있습니다.", textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "아래 버튼을 통해\n생활 기록을 입력해주세요",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onStartInputClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface)
        ) {
            Text(text = "기록 입력하기")
        }
    }
}