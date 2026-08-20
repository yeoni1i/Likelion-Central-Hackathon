package com.example.atocuemobile.ui.screen.timeline.meal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import com.example.atocuemobile.network.RetrofitClient
import com.example.atocuemobile.network.dto.DailyLogResponse
import com.example.atocuemobile.ui.screen.timeline.meal.component.MealRecordCard
import com.example.atocuemobile.ui.screen.timeline.model.MealRecord
import com.example.atocuemobile.ui.screen.timeline.model.MealType
import java.time.LocalDate
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.CancellationException

@Composable
fun MealRecordTab(
    date: LocalDate,
    onAddRecordClick: () -> Unit,
    onRecordClick: (MealRecord) -> Unit
) {

    var dailyLogs by remember {
        mutableStateOf<List<DailyLogResponse>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(date) {
        try {
            isLoading = true

            Log.d(
                "DAILY_LOG_TEST",
                "조회 시작 date=$date"
            )

            val response = RetrofitClient.api.getDailyLogs(
                date = date.toString()
            )

            dailyLogs = response

            Log.d(
                "DAILY_LOG_TEST",
                "식단 조회 성공 date=$date logs=$response"
            )

        } catch (e: CancellationException) {
            // Compose가 LaunchedEffect를 정상적으로 취소한 경우
            // 실패로 처리하면 안 됨
            throw e

        } catch (e: Exception) {

            Log.e(
                "DAILY_LOG_TEST",
                "식단 조회 실제 실패 date=$date",
                e
            )

            dailyLogs = emptyList()

        } finally {
            isLoading = false
        }
    }


    // 식단으로 등록된 daily_log만 추출
    val mealLogs = dailyLogs.filter {
        !it.mealType.isNullOrBlank()
    }

    val records = MealType.entries.map { type ->

        val matchedLogs = mealLogs.filter { log ->
            log.mealType == type.name
        }

        MealRecord(
            date = date,
            mealType = type,

            photoUrl = matchedLogs
                .firstOrNull { !it.imageUrl.isNullOrBlank() }
                ?.imageUrl,

            menuItems = matchedLogs
                .flatMap { it.foods }
                .distinct()
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 24.dp , vertical = 30.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(records) { record ->
            MealRecordCard(
                record = record,
                onClick = {
                    if (
                        !record.photoUrl.isNullOrBlank() ||
                        record.menuItems.isNotEmpty()
                    ) {
                        onRecordClick(record)
                    } else {
                        onAddRecordClick()
                    }
                }
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, color = Color(0xFFEBEBEB), RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFFFFF))
                    .clickable { onAddRecordClick() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "새로운 기록 추가",
                        modifier = Modifier.size(62.dp),
                        tint = Color(0xFF6C6E72)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "새로운 기록 추가",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}