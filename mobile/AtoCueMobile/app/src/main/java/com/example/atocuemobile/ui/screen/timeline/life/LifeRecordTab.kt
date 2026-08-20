package com.example.atocuemobile.ui.screen.timeline.life

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.network.RetrofitClient
import com.example.atocuemobile.network.dto.DailyLogResponse
import com.example.atocuemobile.ui.screen.timeline.ChipBorder
import com.example.atocuemobile.ui.screen.timeline.MainBackGroundColor
import com.example.atocuemobile.ui.screen.timeline.life.component.ShowerMoisturizerSection
import com.example.atocuemobile.ui.screen.timeline.life.component.SymptomSection
import com.example.atocuemobile.ui.screen.timeline.model.ShowerCount
import com.example.atocuemobile.ui.screen.timeline.model.SymptomType
import java.time.LocalDate


@Composable
fun LifeRecordTab(
    date: LocalDate,
    onNavigateToLifeRecordInput: () -> Unit
) {

    // ================================
    // 서버에서 받아온 해당 날짜 전체 DailyLog
    // ================================
    var dailyLogs by remember {
        mutableStateOf<List<DailyLogResponse>>(
            emptyList()
        )
    }

    var isLoading by remember {
        mutableStateOf(false)
    }


    // ================================
    // 선택된 날짜가 바뀔 때마다
    // 서버에서 DailyLog 다시 조회
    // ================================
    LaunchedEffect(date) {

        try {

            isLoading = true

            dailyLogs =
                RetrofitClient.api.getDailyLogs(
                    date = date.toString()
                )

            Log.d(
                "DAILY_LOG_TEST",
                "생활기록 조회 성공 date=$date logs=$dailyLogs"
            )

        } catch (e: Exception) {

            Log.e(
                "DAILY_LOG_TEST",
                "생활기록 조회 실패 date=$date",
                e
            )

            dailyLogs = emptyList()

        } finally {

            isLoading = false
        }
    }


    // ================================
    // 전체 DailyLog 중 생활기록 찾기
    //
    // 식단기록은 mealType / foods를 가지고 있고
    // 생활기록은 아래 값들을 가지고 있음
    // ================================
    val lifeLog =
        dailyLogs.firstOrNull { log ->

            log.showerCount != null ||
                    log.moisturizerCount != null ||
                    log.symptoms.isNotEmpty() ||
                    !log.memo.isNullOrBlank()
        }


    // ================================
    // 로딩 화면
    // ================================
    if (isLoading) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "생활기록을 불러오는 중입니다.",
                color = Color.Gray
            )
        }

        return
    }


    // ================================
    // 생활기록이 없는 경우
    // ================================
    if (lifeLog == null) {

        EmptyLifeRecord(
            onStartInputClick =
                onNavigateToLifeRecordInput
        )

        return
    }


    // =====================================================
    // 백엔드 데이터 → 기존 UI Model 변환
    // =====================================================

    /*
     * 백엔드
     *
     * showerCount = 1 / 2 / 3
     *
     * ↓
     *
     * 기존 UI
     *
     * ShowerCount.ONCE
     * ShowerCount.TWICE
     * ShowerCount.THREE_OR_MORE
     */
    val showerCountUi: ShowerCount =
        when (lifeLog.showerCount) {

            1 -> ShowerCount.ONCE

            2 -> ShowerCount.TWICE

            3 -> ShowerCount.THREE_OR_MORE

            else -> ShowerCount.THREE_OR_MORE
        }


    /*
     * 백엔드
     *
     * [
     *   "심한 가려움증",
     *   "건조증"
     * ]
     *
     * ↓
     *
     * 기존 UI
     *
     * [
     *   SymptomType.SEVERE_ITCH,
     *   SymptomType.DRYNESS
     * ]
     */
    val symptomTypes: List<SymptomType> =
        SymptomType.entries.filter { symptomType ->

            lifeLog.symptoms.any { serverSymptom ->

                serverSymptom == symptomType.label
            }
        }


    // ================================
    // 실제 생활기록 화면
    // ================================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackGroundColor)
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp)
    ) {


        // ================================
        // 수정하기
        // ================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.End
        ) {

            Text(
                text = "수정하기",
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight =
                    FontWeight.Medium,
                modifier =
                    Modifier.clickable {

                        onNavigateToLifeRecordInput()
                    }
            )
        }


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        // ================================
        // 샤워 / 보습제
        // ================================
        ShowerMoisturizerSection(

            showerCount =
                showerCountUi,

            moisturizerCount =
                lifeLog.moisturizerCount ?: 0,

            // 조회 화면이므로 수정 불가능
            isEditMode = false,

            onShowerCountChange = {},

            onMoisturizerCountChange = {}
        )


        Spacer(
            modifier =
                Modifier.height(24.dp)
        )


        // ================================
        // 주요 증상
        // ================================
        SymptomSection(

            selectedSymptoms =
                symptomTypes,

            // 조회 화면
            isEditMode = false,

            onSymptomToggle = {}
        )


        // ================================
        // 특이사항 기록
        // ================================
        if (!lifeLog.memo.isNullOrBlank()) {

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )


            Text(
                text = "특이사항 기록",
                fontSize = 16.sp,
                fontWeight =
                    FontWeight.SemiBold
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            12.dp
                        )
                    )
                    .background(Color.White)
                    .border(
                        width = 1.dp,
                        color = ChipBorder,
                        shape =
                            RoundedCornerShape(
                                12.dp
                            )
                    )
                    .padding(16.dp)
            ) {

                Text(
                    text = lifeLog.memo,
                    fontSize = 14.sp,
                    color = Color.Black,
                    lineHeight = 20.sp
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(30.dp)
        )
    }
}


@Composable
private fun EmptyLifeRecord(
    onStartInputClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackGroundColor)
            .padding(20.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        Text(
            text =
                "생활기록이 비어있습니다.",

            textAlign =
                TextAlign.Center
        )


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        Text(
            text =
                "아래 버튼을 통해\n생활 기록을 입력해주세요",

            textAlign =
                TextAlign.Center,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )


        Spacer(
            modifier =
                Modifier.height(24.dp)
        )


        Button(
            onClick =
                onStartInputClick,

            shape =
                RoundedCornerShape(50),

            colors =
                ButtonDefaults
                    .buttonColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .onSurface
                    )
        ) {

            Text(
                text =
                    "기록 입력하기"
            )
        }
    }
}