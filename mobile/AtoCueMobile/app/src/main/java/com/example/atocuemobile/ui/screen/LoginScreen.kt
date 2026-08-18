package com.example.atocuemobile.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.network.RetrofitClient
import com.example.atocuemobile.network.dto.LoginRequest
import com.example.atocuemobile.ui.component.AuthTextField
import com.example.atocuemobile.ui.component.PrimaryButton
import com.example.atocuemobile.ui.theme.AtoCueMobileTheme
import com.example.atocuemobile.ui.theme.LinkGray
import com.example.atocuemobile.ui.theme.TitleBlack
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun LoginScreen(
    onLoginSuccess: (userId: Long, token: String) -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var idInput by remember { mutableStateOf(TextFieldValue("")) }
    var passwordInput by remember { mutableStateOf(TextFieldValue("")) }
    var idError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "안녕하세요 :)\n아토큐입니다.",
                style = TextStyle(
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
                placeholder = "비밀번호 입력",
                isError = passwordError != null,
                errorMessage = passwordError
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = if (isLoading) "로그인 중..." else "로그인",
                enabled = !isLoading,
                onClick = {
                    idError = null
                    passwordError = null

                    if (idInput.text.isBlank()) {
                        idError = "아이디를 입력해주세요."
                        return@PrimaryButton
                    }
                    if (passwordInput.text.isBlank()) {
                        passwordError = "비밀번호를 입력해주세요."
                        return@PrimaryButton
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            val response = RetrofitClient.api.login(
                                LoginRequest(username = idInput.text, password = passwordInput.text)
                            )
                            onLoginSuccess(response.userId, response.token)
                        } catch (e: HttpException) {
                            when (e.code()) {
                                404 -> idError = "존재하지않는 아이디입니다."
                                401 -> passwordError = "비밀번호가 일치하지 않습니다."
                                else -> idError = "로그인에 실패했습니다. 다시 시도해주세요."
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

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToSignUp) {
            Text(
                text = "회원가입",
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = LinkGray,
                    textDecoration = TextDecoration.Underline
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    AtoCueMobileTheme {
        LoginScreen(
            onLoginSuccess = { _, _ -> },
            onNavigateToSignUp = {}
        )
    }
}