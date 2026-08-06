package com.example.scratchdetecter.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.example.scratchdetecter.network.dto.PairDeviceRequest
import com.example.scratchdetecter.network.RetrofitClient
import com.example.scratchdetecter.presentation.theme.ScratchDetecterTheme
import kotlinx.coroutines.launch

class PairActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ScratchDetecterTheme {
                PairScreen(
                    onFinish = {
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
private fun PairScreen(
    onFinish: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var pairingCode by remember {
        mutableStateOf("")
    }

    var resultMessage by remember {
        mutableStateOf("부모 앱에 표시된 코드를 입력하세요.")
    }

    var isPairing by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF202124))
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 22.dp,
                vertical = 18.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text =
                if (pairingCode.isEmpty()) {
                    "------"
                } else {
                    pairingCode.padEnd(6, '-')
                },
            color = Color(0xFF8ED1FF),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        NumberPad(
            enabled = !isPairing,
            onNumberClick = { number ->
                if (pairingCode.length < 6) {
                    pairingCode += number
                    resultMessage =
                        "부모 앱에 표시된 코드를 입력하세요."
                }
            },
            onDeleteClick = {
                if (pairingCode.isNotEmpty()) {
                    pairingCode =
                        pairingCode.dropLast(1)
                }
            },
            onSubmitClick = {
                if (pairingCode.length != 6) {
                    resultMessage =
                        "6자리 코드를 입력해주세요."
                    return@NumberPad
                }

                isPairing = true
                resultMessage = "등록 중..."

                coroutineScope.launch {
                    try {
                        val response =
                            RetrofitClient
                                .scratchApi
                                .pairDevice(
                                    PairDeviceRequest(
                                        pairingCode =
                                            pairingCode,
                                        deviceId = "1",
                                        deviceName =
                                            "Galaxy Watch4"
                                    )
                                )

                        resultMessage =
                            if (response.isSuccessful) {
                                response.body()
                                    ?.message
                                    ?: "워치가 등록되었습니다."
                            } else {
                                "등록 실패 (${response.code()})"
                            }

                    } catch (exception: Exception) {
                        resultMessage =
                            "연결 실패: ${exception.message}"

                        Log.e(
                            "PAIR_API",
                            "워치 등록 실패",
                            exception
                        )
                    } finally {
                        isPairing = false
                    }
                }
            }
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = resultMessage,
            color =
                when {
                    resultMessage.contains(
                        "등록되었습니다"
                    ) -> Color(0xFF65D665)

                    resultMessage.contains("실패") ->
                        Color(0xFFFF8A80)

                    else ->
                        Color(0xFFB8C0CC)
                },
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

        if (
            resultMessage.contains(
                "등록되었습니다"
            )
        ) {
            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(
                onClick = onFinish,
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                contentPadding =
                    PaddingValues(0.dp)
            ) {
                Text(
                    text = "완료",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PairingCodeDisplay(
    pairingCode: String
) {
    Row(
        horizontalArrangement =
            Arrangement.spacedBy(5.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        repeat(6) { index ->
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .border(
                        width = 1.5.dp,
                        color =
                            if (
                                index <
                                pairingCode.length
                            ) {
                                Color(0xFF8ED1FF)
                            } else {
                                Color(0xFF707782)
                            },
                        shape = CircleShape
                    ),
                contentAlignment =
                    Alignment.Center
            ) {
                if (
                    index <
                    pairingCode.length
                ) {
                    Text(
                        text =
                            pairingCode[index]
                                .toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun NumberPad(
    enabled: Boolean,
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(6.dp)
    ) {
        listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9")
        ).forEach { row ->

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { number ->
                    KeypadButton(
                        label = number,
                        enabled = enabled,
                        containerColor =
                            Color.White,
                        contentColor =
                            Color.Black,
                        onClick = {
                            onNumberClick(number)
                        }
                    )
                }
            }
        }

        Row(
            horizontalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {
            KeypadButton(
                label = "←",
                enabled = enabled,
                containerColor = Color.White,
                contentColor = Color.Black,
                onClick = onDeleteClick
            )

            KeypadButton(
                label = "0",
                enabled = enabled,
                containerColor = Color.White,
                contentColor = Color.Black,
                onClick = {
                    onNumberClick("0")
                }
            )

            KeypadButton(
                label = "✓",
                enabled = enabled,
                containerColor =
                    Color(0xFF8ED1FF),
                contentColor =
                    Color(0xFF111111),
                onClick = onSubmitClick
            )
        }
    }
}

@Composable
private fun KeypadButton(
    label: String,
    enabled: Boolean,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(44.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = contentColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}