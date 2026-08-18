package com.example.atocuemobile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.component.OnboardingProgressBar
import com.example.atocuemobile.ui.component.PrimaryButton
import com.example.atocuemobile.ui.theme.AtoCueMobileTheme
import com.example.atocuemobile.ui.theme.Pretendard
import com.example.atocuemobile.ui.theme.TitleBlack

private val SelectedBg = Color(0x265398FF)
private val SelectedBorder = Color(0x595398FF)
private val SelectedText = Color(0xFF397FE9)

private val UnselectedBg = Color(0xFFF3F4F6)
private val UnselectedText = Color(0xFF000000)

@Composable
fun SkinConditionScreen(
    onNext: (conditions: List<String>) -> Unit,
    onNavigateBack: () -> Unit
) {
    val conditions = listOf(
        "심한 가려움증",
        "붉은 발진",
        "줄까짐",
        "진물과 딱지",
        "피부 태선화",
        "건조증"
    )

    val selectedConditions = remember { mutableStateListOf<String>() }

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

        OnboardingProgressBar(step = 3, totalSteps = 5)

        Spacer(modifier = Modifier.height(36.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
        ) {
            Text(
                text = "주요 피부 질환은 무엇인가요?",
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

            Spacer(modifier = Modifier.height(28.dp))

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                conditions.forEach { condition ->
                    val isSelected = selectedConditions.contains(condition)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (isSelected) SelectedBg else UnselectedBg,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .then(
                                if (isSelected) Modifier.border(1.dp, SelectedBorder, RoundedCornerShape(10.dp))
                                else Modifier
                            )
                            .clickable {
                                if (isSelected) selectedConditions.remove(condition)
                                else selectedConditions.add(condition)
                            }
                            .padding(PaddingValues(horizontal = 20.dp, vertical = 16.dp)),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = condition,
                                style = TextStyle(
                                    fontFamily = Pretendard,
                                    fontSize = 18.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    lineHeight = 27.sp,
                                    color = if (isSelected) SelectedText else UnselectedText
                                )
                            )

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                    tint = SelectedText
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "다음",
                enabled = true,
                onClick = {
                    onNext(selectedConditions.toList())
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SkinConditionScreenPreview() {
    AtoCueMobileTheme {
        SkinConditionScreen(
            onNext = {},
            onNavigateBack = {}
        )
    }
}