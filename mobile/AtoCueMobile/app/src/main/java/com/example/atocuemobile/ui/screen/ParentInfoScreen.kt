package com.example.atocuemobile.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.component.AuthTextField
import com.example.atocuemobile.ui.component.OnboardingProgressBar
import com.example.atocuemobile.ui.component.PrimaryButton
import com.example.atocuemobile.ui.theme.AtoCueMobileTheme
import com.example.atocuemobile.ui.theme.Pretendard
import com.example.atocuemobile.ui.theme.TitleBlack
import androidx.compose.ui.text.input.TextFieldValue
@Composable
fun ParentInfoScreen(
    onNext: (parentName: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var nameInput by remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
            }
        }

        OnboardingProgressBar(step = 1, totalSteps = 5)

        Spacer(modifier = Modifier.height(36.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
        ) {
            Text(
                text = "보호자의 이름을 입력해주세요",
                style = TextStyle(
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    lineHeight = 33.sp,
                    color = TitleBlack
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "리포트 분석에 필요해요! 외부에 공개되지 않아요",
                style = TextStyle(
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = TitleBlack
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthTextField(
                value = nameInput,
                onValueChange = {
                    nameInput = it
                    errorMessage = null
                },
                placeholder = "이름 입력",
                isError = errorMessage != null,
                errorMessage = errorMessage,
                showClearButton = true
            )

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "다음",
                enabled = true,
                onClick = {
                    if (nameInput.text.isBlank()) {
                        errorMessage = "이름을 입력해주세요."
                        return@PrimaryButton
                    }
                    onNext(nameInput.text)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ParentInfoScreenPreview() {
    AtoCueMobileTheme {
        ParentInfoScreen(
            onNext = {},
            onNavigateBack = {}
        )
    }
}