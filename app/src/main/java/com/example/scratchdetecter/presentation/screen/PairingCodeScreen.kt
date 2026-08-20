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
    val keyboardController =
        LocalSoftwareKeyboardController.current

    var code by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    /*
     * 6자리 페어링 코드 서버 전송
     */
    suspend fun submit() {

        if (
            code.length != 6 ||
            isLoading
        ) {
            return
        }

        isLoading = true
        errorMessage = null

        // 키보드 먼저 닫기
        keyboardController?.hide()

        try {

            /*
             * 실제 워치 고유 ID
             */
            val androidId =
                Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
                    ?: error(
                        "워치 ID를 가져올 수 없습니다."
                    )

            /*
             * 실제 워치 모델명
             */
            val deviceName =
                Build.MODEL

            /*
             * Spring Boot 페어링 API 호출
             */
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
                    response
                        .errorBody()
                        ?.string()

                error(
                    "연동 실패 (${response.code()})" +
                            if (
                                errorBody.isNullOrBlank()
                            ) {
                                ""
                            } else {
                                ": $errorBody"
                            }
                )
            }

            val body =
                response.body()
                    ?: error(
                        "서버 응답이 없습니다."
                    )

            val serverDeviceId =
                body
                    .deviceId
                    ?.toLongOrNull()
                    ?: error(
                        "서버 deviceId가 없습니다."
                    )

            /*
             * MainActivity로 성공 전달
             */
            onPairingSuccess(
                serverDeviceId,
                deviceName
            )

        } catch (
            exception: Exception
        ) {

            errorMessage =
                exception.message
                    ?: "연동에 실패했습니다."

            // 실패하면 다시 입력하도록 초기화
            code = ""

        } finally {

            isLoading = false
        }
    }

    /*
     * 6자리 입력 완료 시 자동 제출
     */
    LaunchedEffect(code) {

        if (
            code.length == 6 &&
            !isLoading
        ) {

            delay(250L)

            submit()
        }
    }

    /*
     * 전체 화면
     */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF181818)
            )
    ) {

        /*
         * 상단 입력 영역
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .background(
                    Color(0xFF202020)
                )
                .padding(
                    start = 22.dp,
                    end = 16.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            /*
             * 코드 입력창
             *
             * weight 사용:
             * X 버튼을 제외한 나머지 공간을 전부 사용.
             *
             * 기존 130dp 고정폭보다
             * 원형 워치 화면에 안정적.
             */
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

                modifier = Modifier
                    .weight(1f)
                    .height(60.dp),

                /*
                 * 투명 TextStyle 사용하지 않음.
                 *
                 * Wear OS IME의 전체화면 입력 모드에서도
                 * 입력값 전체가 정상 표시되도록 실제 텍스트 유지.
                 */
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Start
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
                        Color.White
                    ),

                decorationBox = {
                        innerTextField ->

                    Box(
                        modifier =
                            Modifier.fillMaxSize(),
                        contentAlignment =
                            Alignment.CenterStart
                    ) {

                        /*
                         * 코드가 없을 때만 placeholder 표시
                         */
                        if (
                            code.isEmpty()
                        ) {

                            Text(
                                text =
                                    "6자리 코드입력",
                                color =
                                    Color(
                                        0xFF77777F
                                    ),
                                fontSize =
                                    17.sp,
                                maxLines = 1
                            )
                        }

                        /*
                         * 실제 입력창
                         */
                        innerTextField()
                    }
                }
            )

            /*
             * 닫기 버튼
             */
            Text(
                text = "×",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight =
                    FontWeight.Light,
                modifier = Modifier
                    .width(42.dp)
                    .clickable {
                        keyboardController
                            ?.hide()

                        onClose()
                    },
                textAlign =
                    TextAlign.Center
            )
        }

        /*
         * 연동 상태 / 오류 메시지
         */
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
                if (
                    errorMessage != null
                ) {
                    Color(
                        0xFFFF8A80
                    )
                } else {
                    Color.White
                },

            fontSize =
                if (
                    errorMessage != null
                ) {
                    13.sp
                } else {
                    18.sp
                },

            textAlign =
                TextAlign.Center,

            modifier = Modifier
                .align(
                    Alignment.Center
                )
                .padding(
                    horizontal = 24.dp
                )
        )
    }
}