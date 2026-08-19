package com.example.atocuemobile.ui.screen.timeline.life.component

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
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
    isEditMode: Boolean = true, // 편집 모드 구분
    onClick: () -> Unit
) {
    // 🌟 핵심: 편집 모드(isEditMode == true)에서 선택했을 때만 파란 테두리!
    // 조회 화면(isEditMode == false)에서는 무조건 회색 테두리(ChipBorder)로 보입니다.
    val borderColor = if (isEditMode && selected) AtoCueBlue else ChipBorder
    val borderWidth = if (isEditMode && selected) 1.5.dp else 1.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(
                width = borderWidth,
                color = borderColor, // 👈 조회 페이지에서는 무조건 ChipBorder(회색) 적용
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = isEditMode) { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = symptom.iconRes),
            contentDescription = symptom.label,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = symptom.label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = Color.Black
        )
    }
}