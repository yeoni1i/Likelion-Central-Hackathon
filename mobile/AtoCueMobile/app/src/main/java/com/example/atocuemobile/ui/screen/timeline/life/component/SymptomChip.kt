package com.example.atocuemobile.ui.screen.timeline.life.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.screen.timeline.AtoCueBlue
import com.example.atocuemobile.ui.screen.timeline.ChipBorder
import com.example.atocuemobile.ui.screen.timeline.model.SymptomType

@Composable
fun SymptomChip(
    symptom: SymptomType,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) AtoCueBlue else ChipBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TODO: 여기 직접 export한 아이콘으로 교체
        // Image(painter = painterResource(id = symptom.iconRes), contentDescription = symptom.label, modifier = Modifier.size(48.dp))
        Box(modifier = Modifier.size(48.dp)) // 임시 자리 표시용 빈 박스

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = symptom.label, fontSize = 13.sp, fontWeight = FontWeight.Normal)
    }
}