package com.example.atocuemobile.ui.screen.timeline.life.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.atocuemobile.ui.screen.timeline.model.ShowerCount

@Composable
fun ShowerMoisturizerSection(
    showerCount: ShowerCount,
    moisturizerCount: Int,
    isEditMode: Boolean,
    onShowerCountChange: (ShowerCount) -> Unit,
    onMoisturizerCountChange: (Int) -> Unit
) {
    if (!isEditMode) {
        // ===== 조회 모드 (두번째 사진) =====
        Column {
            Text(text = "샤워, 보습제")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "샤워")
                    Text(text = "${showerCount.count}회")
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "보습횟수")
                    Text(text = "${moisturizerCount}회")
                }
            }
        }
        return
    }

    // ===== 수정 모드 (첫번째 사진, 아코디언) =====
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
            Text(text = "샤워, 보습제")
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }

        if (expanded) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(text = "샤워 횟수")
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShowerCount.entries.forEach { option ->
                        ShowerCountButton(
                            label = option.label,
                            selected = option == showerCount,
                            onClick = { onShowerCountChange(option) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "보습제 사용횟수")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${moisturizerCount}회",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                    // TODO: 피그마처럼 슬라이더 손잡이 위에 말풍선으로 값 표시하려면
                    // 커스텀 Slider(SliderDefaults.Thumb 커스터마이징) 구현 필요.
                    // 지금은 값 텍스트를 슬라이더 위에 표시하는 걸로 대체
                )
                Slider(
                    value = moisturizerCount.toFloat(),
                    onValueChange = { onMoisturizerCountChange(it.toInt()) },
                    valueRange = 1f..20f,
                    steps = 18
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "1회")
                    Text(text = "10회")
                    Text(text = "20회")
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ShowerCountButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label)
        // TODO: selected일 때 글자색 흰색으로, 아닐 때 검정으로 (MaterialTheme.colorScheme.onPrimary 등 적용)
    }
}