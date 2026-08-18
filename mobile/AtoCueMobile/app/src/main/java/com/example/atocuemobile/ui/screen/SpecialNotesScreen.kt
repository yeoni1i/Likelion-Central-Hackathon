package com.example.atocuemobile.ui.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.component.OnboardingProgressBar
import com.example.atocuemobile.ui.component.PrimaryButton
import com.example.atocuemobile.ui.theme.AtoCueMobileTheme
import com.example.atocuemobile.ui.theme.BorderGray
import com.example.atocuemobile.ui.theme.LabelGray
import com.example.atocuemobile.ui.theme.Pretendard
import com.example.atocuemobile.ui.theme.TitleBlack

private val TextGrayColor = Color(0xFF6C6E72)

@Composable
fun SpecialNotesScreen(
    onNext: (specialNote: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    // 한글 입력을 위해 TextFieldValue로 초기화
    var textInput by remember { mutableStateOf(TextFieldValue("")) }
    val maxChar = 500

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
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

        OnboardingProgressBar(step = 4, totalSteps = 5)

        Spacer(modifier = Modifier.height(36.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
        ) {
            Text(
                text = "특이사항을 입력해주세요",
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

            Spacer(modifier = Modifier.height(36.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                if (textInput.text.isEmpty()) {
                    Column {
                        Text(
                            text = "아이의 피부질환 중 특이한 사항 혹은\n알레르기, 건강사항을 적어주세요",
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 21.sp,
                                color = TextGrayColor
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "예시 )\n아토피가 원래 심하지 않았으나, 호르몬제 투약 이후\n갑자기 아토피가 심해진 케이스 입니다. 알레르기는\n고양이 털 알레르기가 있습니다.",
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 18.sp,
                                color = TextGrayColor
                            )
                        )
                    }
                }

                BasicTextField(
                    value = textInput,
                    onValueChange = {
                        if (it.text.length <= maxChar) textInput = it
                    },
                    textStyle = TextStyle(
                        fontFamily = Pretendard,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = TitleBlack
                    ),
                    modifier = Modifier.fillMaxSize()
                )

                Text(
                    text = "${textInput.text.length}/$maxChar",
                    style = TextStyle(
                        fontFamily = Pretendard,
                        fontSize = 12.sp,
                        color = LabelGray
                    ),
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "다음",
                enabled = true,
                onClick = {
                    onNext(textInput.text)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SpecialNotesScreenPreview() {
    AtoCueMobileTheme {
        SpecialNotesScreen(
            onNext = {},
            onNavigateBack = {}
        )
    }
}