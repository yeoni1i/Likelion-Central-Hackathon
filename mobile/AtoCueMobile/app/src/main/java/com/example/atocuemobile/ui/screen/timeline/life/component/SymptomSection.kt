package com.example.atocuemobile.ui.screen.timeline.life.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.screen.timeline.ChipBorder
import com.example.atocuemobile.ui.screen.timeline.model.SymptomType

@Composable
fun SymptomSection(
    selectedSymptoms: List<SymptomType>,
    isEditMode: Boolean,
    onSymptomToggle: (SymptomType) -> Unit
) {
    if (!isEditMode) {
        Column {
            Text(text = "주요증상", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            val rowCount = ((selectedSymptoms.size + 1) / 2).coerceAtLeast(1)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                userScrollEnabled = false,
                modifier = Modifier.height((rowCount * 124).dp)
            ) {
                items(selectedSymptoms) { symptom ->
                    SymptomChip(
                        symptom = symptom,
                        selected = true,
                        isEditMode = false, // 🌟 요기! isEditMode = false를 붙여주어서 파란 테두리를 제거했습니다!
                        onClick = {}
                    )
                }
            }
        }
        return
    }

    var expanded by remember { mutableStateOf(true) }

    // 🌟 화이트 + 테두리 아웃라인
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, ChipBorder, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "주요 증상", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }

        if (expanded) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
                modifier = Modifier.height(360.dp)
            ) {
                items(SymptomType.entries) { symptom ->
                    SymptomChip(
                        symptom = symptom,
                        selected = symptom in selectedSymptoms,
                        isEditMode = true,
                        onClick = { onSymptomToggle(symptom) }
                    )
                }
            }
        }
    }
}