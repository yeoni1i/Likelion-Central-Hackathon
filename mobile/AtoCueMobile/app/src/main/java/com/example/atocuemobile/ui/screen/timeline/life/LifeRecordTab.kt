package com.example.atocuemobile.ui.screen.timeline.life

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.atocuemobile.ui.screen.timeline.life.component.ShowerMoisturizerSection
import com.example.atocuemobile.ui.screen.timeline.life.component.SymptomSection
import com.example.atocuemobile.ui.screen.timeline.model.LifeRecord
import java.time.LocalDate

@Composable
fun LifeRecordTab(
    date: LocalDate,
    onNavigateToLifeRecordInput: () -> Unit  // 기록 입력/수정 화면으로 이동시키는 콜백
) {
    // TODO: ViewModel에서 date 기준으로 실제 저장된 기록 있는지 불러오도록 교체
    var record by remember { mutableStateOf(LifeRecord()) }
    var hasRecord by remember { mutableStateOf(false) }

    if (!hasRecord) {
        EmptyLifeRecord(onStartInputClick = onNavigateToLifeRecordInput)
        return
    }

    // ===== 기록 있음: 조회 전용 (수정하기 눌러도 입력 화면으로 이동) =====
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "수정하기",
                modifier = Modifier.clickable { onNavigateToLifeRecordInput() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ShowerMoisturizerSection(
            showerCount = record.showerCount,
            moisturizerCount = record.moisturizerCount,
            isEditMode = false,
            onShowerCountChange = {},
            onMoisturizerCountChange = {}
        )

        Spacer(modifier = Modifier.height(24.dp))

        SymptomSection(
            selectedSymptoms = record.symptoms,
            isEditMode = false,
            onSymptomToggle = {}
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (record.note.isNotBlank()) {
            Text(text = "특이사항 기록")
            Text(text = record.note)
        }
    }
}

@Composable
private fun EmptyLifeRecord(
    onStartInputClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
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