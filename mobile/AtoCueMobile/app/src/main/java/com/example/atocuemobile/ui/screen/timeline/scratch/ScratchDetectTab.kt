package com.example.atocuemobile.ui.screen.timeline.scratch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.atocuemobile.ui.screen.timeline.scratch.component.ScratchEventItem
import com.example.atocuemobile.ui.screen.timeline.model.ScratchEvent
import java.time.LocalDate
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp


@Composable
fun ScratchDetectTab(
    date: LocalDate
    // TODO: 지금은 파라미터만 받고 실제 데이터 연결은 안 함.
    // 나중에 ViewModel에서 date 기준으로 이벤트 리스트 가져오도록 교체
) {
    val events: List<ScratchEvent> = remember { emptyList() }
    // TODO: 더미 데이터 넣거나 ViewModel.collectAsState()로 교체

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text(text = "오늘의 경고 타임라인")
        Spacer(modifier = Modifier.height(12.dp))

        if (events.isEmpty()) {
            Text(text = "긁음 기록 없음")
            // TODO: 시간대별로 "긁음 기록 없음" / 이벤트 리스트 섞어서 보여주는
            // 레이아웃은 디자인(01:00 AM, 02:00 AM 구간 표시)에 맞춰 추가 작업 필요
        } else {
            LazyColumn {
                items(events) { event -> ScratchEventItem(event) }
            }
        }
    }
}