package com.example.atocuemobile.ui.screen.timeline.life.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.atocuemobile.ui.screen.timeline.model.SymptomType

@Composable
fun SymptomSection(
    selectedSymptoms: List<SymptomType>,
    isEditMode: Boolean,
    onSymptomToggle: (SymptomType) -> Unit
) {
    if (!isEditMode) {
        // ===== 조회 모드: 선택된 증상만 표시 (두번째 사진) =====
        Column {
            Text(text = "주요증상")
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(
                    // 선택된 증상 개수에 맞춰 대략적인 높이 계산 (2열 기준)
                    (((selectedSymptoms.size + 1) / 2) * 110).dp
                )
                // TODO: LazyVerticalGrid를 부모 스크롤 안에서 높이 자동 계산하려면
                // FlowRow(androidx.compose.foundation.layout.FlowRow)로 바꾸는 게 더 깔끔할 수 있음
            ) {
                items(selectedSymptoms) { symptom ->
                    SymptomChip(symptom = symptom, selected = true, onClick = {})
                }
            }
        }
        return
    }

    // ===== 수정 모드: 아코디언 + 전체 증상 선택 (첫번째 사진) =====
    var expanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "주요 증상")
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }

        if (expanded) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.height(340.dp)
                // TODO: SymptomType 개수(6개) 기준 고정 높이. 개수 늘어나면 계산식 조정 필요
            ) {
                items(SymptomType.entries) { symptom ->
                    SymptomChip(
                        symptom = symptom,
                        selected = symptom in selectedSymptoms,
                        onClick = { onSymptomToggle(symptom) }
                    )
                }
            }
        }
    }
}