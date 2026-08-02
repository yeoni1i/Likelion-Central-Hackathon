package com.example.atocuemobile.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.atocuemobile.network.RetrofitClient
import com.example.atocuemobile.network.dto.PairingCodeResponse
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PairingTestScreen() {

    var result by remember {
        mutableStateOf<PairingCodeResponse?>(null)
    }

    var statusMessage by remember {
        mutableStateOf("등록 코드를 생성해주세요.")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "워치 등록 API 테스트",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "아래 버튼을 누르면 워치에서 입력할 6자리 등록 코드가 생성됩니다."
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            onClick = {
                coroutineScope.launch {
                    isLoading = true
                    result = null
                    statusMessage = "등록 코드 생성 중..."

                    try {
                        val response =
                            RetrofitClient.api.createPairingCode(
                                parentUserId = 1L
                            )

                        result = response
                        statusMessage = "등록 코드 생성 성공"
                    } catch (e: Exception) {
                        statusMessage =
                            "생성 실패: ${e.message ?: "알 수 없는 오류"}"
                    } finally {
                        isLoading = false
                    }
                }
            }
        ) {
            Text(
                if (isLoading) {
                    "생성 중..."
                } else {
                    "등록 코드 생성"
                }
            )
        }

        Text("상태: $statusMessage")

        Spacer(modifier = Modifier.height(8.dp))

        result?.let { data ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "워치에 입력할 등록 코드",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = data.pairingCode,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "만료 시각: ${data.expiresAt}"
                    )
                }
            }
        }
    }
}

private fun formatDateTime(time: String): String {
    return Instant.parse(time)
        .atZone(ZoneId.of("Asia/Seoul"))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}