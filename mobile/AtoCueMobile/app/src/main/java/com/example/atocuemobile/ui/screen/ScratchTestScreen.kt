package com.example.atocuemobile.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.items
import com.example.atocuemobile.network.dto.ScratchTimelineResponse
import com.example.atocuemobile.network.RetrofitClient
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun ScratchTestScreen() {

    var userId by remember {
        mutableStateOf("1")
    }

    var date by remember {
        mutableStateOf(LocalDate.now().toString())
    }

    var result by remember {
        mutableStateOf<ScratchTimelineResponse?>(null)
    }

    var statusMessage by remember {
        mutableStateOf("조회 전")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "하루 요약 API 테스트",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = userId,
            onValueChange = {
                userId = it
            },
            label = {
                Text("사용자 ID")
            },
            singleLine = true
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = date,
            onValueChange = {
                date = it
            },
            label = {
                Text("조회 날짜")
            },
            supportingText = {
                Text("YYYY-MM-DD 형식")
            },
            singleLine = true
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            onClick = {
                statusMessage = "버튼 클릭됨"

                val parsedUserId = userId.toLongOrNull()
                val cleanDate = date.trim()

                if (parsedUserId == null) {
                    statusMessage = "사용자 ID를 숫자로 입력해주세요."
                    return@Button
                }

                coroutineScope.launch {
                    isLoading = true
                    result = null
                    statusMessage = "조회 중..."

                    try {
                        val response =
                            RetrofitClient.api.getScratchEvents(
                                userId = parsedUserId,
                                date = cleanDate,
                                timezone = "Asia/Seoul"
                            )

                        result = response
                        statusMessage = "조회 성공"
                    } catch (e: Exception) {
                        statusMessage =
                            "조회 실패: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            }
        ) {
            Text(
                if (isLoading) {
                    "조회 중..."
                } else {
                    "긁음 기록 조회"
                }
            )
        }

        Text("요청 API: GET /scratch/events")
        Text("상태: $statusMessage")

        Spacer(modifier = Modifier.height(8.dp))

        result?.let { data ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(data.events) { event ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = event.startTs,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text("${event.durationSec}초")
                            }

                            Text("강도 ${event.intensity}")
                        }
                    }
                }
            }
        }
    }
}