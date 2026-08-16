package com.example.atocuemobile.ui.screen.record.meal.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun MenuInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    onRemoveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { androidx.compose.material3.Text("메뉴 입력") },
            modifier = Modifier.weight(1f)
            // TODO: 테두리 없는 스타일 원하면 colors = TextFieldDefaults.colors(...)로 배경/테두리 투명 처리
        )
        Icon(
            imageVector = Icons.Default.Remove,
            contentDescription = "메뉴 삭제",
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable { onRemoveClick() }
        )
    }
}