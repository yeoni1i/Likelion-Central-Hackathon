package com.example.scratchdetecter.presentation.screen

import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Text
import com.example.scratchdetecter.network.RetrofitClient
import com.example.scratchdetecter.network.dto.PairDeviceRequest
import kotlinx.coroutines.delay

@Composable
fun PairingCodeScreen(
    onPairingSuccess: (Long?, String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var code by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    suspend fun submit() {
        if (code.length != 6 || isLoading) {
            return
        }

        isLoading = true
        errorMessage = null

        try {
            // 워치 고유 Android ID
            val androidId =
                Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                ) ?: error("워치 ID를 가져올 수 없습니다.")

            // 실제 워치 모델명
            val deviceName =
                Build.MODEL

            val response =
                RetrofitClient
                    .scratchApi
                    .pairDevice(
                        PairDeviceRequest(
                            pairingCode = code,
                            deviceId = androidId,
                            deviceName = deviceName
                        )
                    )

            if (!response.isSuccessful) {
                val errorBody =
                    response.errorBody()
                        ?.string()

                error(
                    "연동 실패 (${response.code()})" +
                            if (errorBody.isNullOrBlank()) {
                                ""
                            } else {
                                ": $errorBody"
                            }
                )
            }

            val body =
                response.body()
                    ?: error("서버 응답이 없습니다.")

            val serverDeviceId =
                body.deviceId
                    ?.toLongOrNull()
                    ?: error("서버 deviceId가 없습니다.")

            onPairingSuccess(
                serverDeviceId,
                deviceName
            )

        } catch (exception: Exception) {
            errorMessage =
                exception.message
                    ?: "연동에 실패했습니다."

            code = ""

        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(code) {
        if (code.length == 6) {
            delay(250L)
            submit()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF181818)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .background(
                    Color(0xFF202020)
                )
                .padding(
                    start = 28.dp,
                    end = 22.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            BasicTextField(
                value = code,
                onValueChange = { input ->
                    if (!isLoading) {
                        code =
                            input
                                .filter(
                                    Char::isDigit
                                )
                                .take(6)
                    }
                },
                modifier =
                    Modifier.width(130.dp),
                textStyle =
                    TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        textAlign =
                            TextAlign.Start
                    ),
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number,
                        imeAction =
                            ImeAction.Done
                    ),
                keyboardActions =
                    KeyboardActions(
                        onDone = {
                            keyboardController
                                ?.hide()
                        }
                    ),
                singleLine = true,
                cursorBrush =
                    SolidColor(
                        Color(0xFF8AB4F8)
                    ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier =
                            Modifier.fillMaxWidth(),
                        contentAlignment =
                            Alignment.CenterStart
                    ) {
                        if (code.isEmpty()) {
                            Text(
                                text = "6자리 코드입력",
                                color =
                                    Color(0xFF77777F),
                                fontSize = 19.sp
                            )
                        }

                        innerTextField()
                    }
                }
            )

            Text(
                text = "×",
                color = Color.White,
                fontSize = 46.sp,
                fontWeight =
                    FontWeight.Light,
                modifier =
                    Modifier
                        .width(44.dp)
                        .clickable(
                            onClick = onClose
                        ),
                textAlign =
                    TextAlign.Center
            )
        }

        Text(
            text =
                when {
                    isLoading ->
                        "연동 중..."

                    errorMessage != null ->
                        errorMessage!!

                    else ->
                        ""
                },
            color =
                if (errorMessage != null) {
                    Color(0xFFFF8A80)
                } else {
                    Color.White
                },
            fontSize =
                if (errorMessage != null) {
                    14.sp
                } else {
                    18.sp
                },
            textAlign =
                TextAlign.Center,
            modifier =
                Modifier
                    .align(
                        Alignment.Center
                    )
                    .padding(
                        horizontal = 24.dp
                    )
        )
    }
}