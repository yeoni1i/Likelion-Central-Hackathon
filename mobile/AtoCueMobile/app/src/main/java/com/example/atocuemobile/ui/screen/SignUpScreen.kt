package com.example.atocuemobile.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.atocuemobile.ui.component.AuthTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.network.RetrofitClient
import com.example.atocuemobile.network.dto.SignUpRequest
import com.example.atocuemobile.ui.component.PrimaryButton
import com.example.atocuemobile.ui.theme.Pretendard
import com.example.atocuemobile.ui.theme.TitleBlack
import kotlinx.coroutines.launch
import retrofit2.HttpException

import androidx.compose.ui.tooling.preview.Preview
import com.example.atocuemobile.ui.theme.AtoCueMobileTheme
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var idInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var idError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val passwordRule = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding() // 👈 화면 전체를 상단바 아래로 내림
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

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "회원가입",
                style = TextStyle(
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    lineHeight = 33.sp,
                    color = TitleBlack
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthTextField(
                value = idInput,
                onValueChange = { idInput = it; idError = null },
                placeholder = "아이디 입력",
                isError = idError != null,
                errorMessage = idError
            )

            Spacer(modifier = Modifier.height(20.dp))

            AuthTextField(
                value = passwordInput,
                onValueChange = { passwordInput = it; passwordError = null },
                placeholder = "비밀번호 입력 (영문+숫자 8~16자)",
                isError = passwordError != null,
                errorMessage = passwordError
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = if (isLoading) "가입 중..." else "회원가입", // 시안대로 텍스트 변경
                enabled = !isLoading,
                onClick = {
                    idError = null
                    passwordError = null

                    if (idInput.isBlank()) {
                        idError = "아이디를 입력해주세요."
                        return@PrimaryButton
                    }
                    if (!passwordRule.matches(passwordInput)) {
                        passwordError = "비밀번호는 영문+숫자 8~16자 조합이어야합니다."
                        return@PrimaryButton
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            RetrofitClient.api.signUp(
                                SignUpRequest(username = idInput, password = passwordInput)
                            )
                            onSignUpSuccess()
                        } catch (e: HttpException) {
                            when (e.code()) {
                                409 -> idError = "이미 존재하는 아이디입니다."
                                400 -> passwordError = "비밀번호는 영문+숫자 8~16자 조합이어야합니다."
                                else -> idError = "회원가입에 실패했습니다. 다시 시도해주세요."
                            }
                        } catch (e: Exception) {
                            idError = "네트워크 오류가 발생했습니다. 연결을 확인해주세요."
                        } finally {
                            isLoading = false
                        }
                    }
                }
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpScreenPreview() {
    AtoCueMobileTheme {
        SignUpScreen(
            onSignUpSuccess = {},
            onNavigateBack = {}
        )
    }
}