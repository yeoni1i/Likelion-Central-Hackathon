package com.example.atocuemobile.ui.screen.timeline.life.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.screen.timeline.AtoCueBlue
import com.example.atocuemobile.ui.screen.timeline.CardBackground
import com.example.atocuemobile.ui.screen.timeline.ChipBorder
import com.example.atocuemobile.ui.screen.timeline.SliderTrackGray
import com.example.atocuemobile.ui.screen.timeline.model.ShowerCount
import androidx.compose.material3.ExperimentalMaterial3Api

@Composable
fun ShowerMoisturizerSection(
    showerCount: ShowerCount,
    moisturizerCount: Int,
    isEditMode: Boolean,
    onShowerCountChange: (ShowerCount) -> Unit,
    onMoisturizerCountChange: (Int) -> Unit
) {
    if (!isEditMode) {
        Column {
            Text(text = "샤워, 보습제", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .padding(vertical = 20.dp)
            ) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "샤워", fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${showerCount.count}회", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.width(1.dp).height(36.dp).background(ChipBorder))
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "보습횟수", fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${moisturizerCount}회", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    var expanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "샤워, 보습제", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }

        if (expanded) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                Text(text = "샤워 횟수", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ShowerCount.entries.forEach { option ->
                        ShowerCountButton(
                            label = option.label,
                            selected = option == showerCount,
                            onClick = { onShowerCountChange(option) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "보습제 사용횟수", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(12.dp))

                MoisturizerSlider(
                    value = moisturizerCount,
                    onValueChange = onMoisturizerCountChange
                )

                Spacer(modifier = Modifier.height(20.dp))
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
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) AtoCueBlue else Color.White)
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = ChipBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) Color.White else Color.Black
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoisturizerSlider(
    value: Int,
    onValueChange: (Int) -> Unit
) {
    var trackWidthPx by remember { mutableStateOf(0) }
    val density = androidx.compose.ui.platform.LocalDensity.current
    val fraction = ((value - 1).coerceIn(0, 19)) / 19f

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { trackWidthPx = it.width }
        ) {
            // 말풍선 (값 표시), 슬라이더 진행률에 맞춰 위치 이동
            val bubbleOffsetDp = with(density) { (trackWidthPx * fraction).toDp() }
            Box(
                modifier = Modifier
                    .offset(x = bubbleOffsetDp - 16.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(AtoCueBlue)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(text = "${value}회", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 1f..20f,
            steps = 18,
            thumb = {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color.White, CircleShape)
                        .border(2.dp, AtoCueBlue, CircleShape)
                )
            },
            colors = SliderDefaults.colors(
                activeTrackColor = AtoCueBlue,
                inactiveTrackColor = SliderTrackGray
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "1회", fontSize = 12.sp, color = Color.Gray)
            Text(text = "10회", fontSize = 12.sp, color = Color.Gray)
            Text(text = "20회", fontSize = 12.sp, color = Color.Gray)
        }
    }
}