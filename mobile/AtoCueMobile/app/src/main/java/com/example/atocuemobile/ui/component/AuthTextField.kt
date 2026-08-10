package com.example.atocuemobile.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

private val BorderGrayColor = Color(0xFFD0D6DD)
private val ErrorColor = Color(0xFFE5484D)
private val PlaceholderColor = Color(0xFF9AA1AC)
private val IconClearColor = Color(0xFFD0D6DD)

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    showClearButton: Boolean = false, // 👈 기본값 false로 설정! 필요할 때만 true로 사용
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val borderColor = if (isError) ErrorColor else BorderGrayColor

    Box {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            cursorBrush = SolidColor(Color(0xFF5398FF)),
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp)
                .border(
                    width = 1.5.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(8.dp)
                ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(placeholder, color = PlaceholderColor)
                        }
                        innerTextField()
                    }

                    // 👈 showClearButton이 true이고 텍스트가 있을 때만 X 버튼 노출
                    if (showClearButton && value.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "텍스트 지우기",
                            tint = IconClearColor,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onValueChange("") }
                        )
                    }
                }
            }
        )
    }

    if (isError && errorMessage != null) {
        Text(
            text = errorMessage,
            color = ErrorColor,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
        )
    }
}