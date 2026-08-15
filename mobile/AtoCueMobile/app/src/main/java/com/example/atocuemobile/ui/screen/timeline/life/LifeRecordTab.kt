package com.example.atocuemobile.ui.screen.timeline.life

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
fun LifeRecordTab(date: LocalDate) {
    // TODO: ViewModel에서 date 기준으로 실제 저장된 기록 있는지 불러오도록 교체
    var record by remember { mutableStateOf(LifeRecord()) }
    var hasRecord by remember { mutableStateOf(false) } // 이 날짜에 기록을 한 번이라도 저장했는지
    var isEditMode by remember { mutableStateOf(false) }

    if (!hasRecord) {
        // ===== 왼쪽 사진: 기록 없음 상태 =====
        EmptyLifeRecord(
            onStartInputClick = {
                hasRecord = true
                isEditMode = true // 바로 입력(수정) 모드로 진입
            }
        )
        return
    }

    // ===== 오른쪽 사진: 기록 있음 상태 (기존 구현) =====
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
                text = if (isEditMode) "완료" else "수정하기",
                modifier = Modifier.clickable {
                    isEditMode = !isEditMode
                    // TODO: "완료" 눌렀을 때 실제 저장(API 호출) 로직 연결 필요
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ShowerMoisturizerSection(
            showerCount = record.showerCount,
            moisturizerCount = record.moisturizerCount,
            isEditMode = isEditMode,
            onShowerCountChange = { record = record.copy(showerCount = it) },
            onMoisturizerCountChange = { record = record.copy(moisturizerCount = it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        SymptomSection(
            selectedSymptoms = record.symptoms,
            isEditMode = isEditMode,
            onSymptomToggle = { symptom ->
                record = record.copy(
                    symptoms = if (symptom in record.symptoms)
                        record.symptoms - symptom
                    else
                        record.symptoms + symptom
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isEditMode) {
            Text(text = "특이사항 기록")
            OutlinedTextField(
                value = record.note,
                onValueChange = { record = record.copy(note = it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("예: 보습제를 다써서 급하게 기존과 다른 제품을 사용함. 제품은 00사의 0000") }
            )
        } else if (record.note.isNotBlank()) {
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
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "생활기록이 비어있습니다.",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
            // TODO: 정확한 폰트 굵기/사이즈는 피그마 값으로 교체
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "아래 버튼을 통해\n생활 기록을 입력해주세요",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
            // TODO: 정확한 회색 hex로 교체하고 싶으면 TextSecondary 같은 변수 만들어서 적용
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onStartInputClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface
                // TODO: 스크린샷은 진한 회색/검정 배경 + 흰 글씨.
                // 정확한 hex 있으면 Color.kt에 값 만들어서 여기 적용
            )
        ) {
            Text(text = "기록 입력하기")
        }
    }
}