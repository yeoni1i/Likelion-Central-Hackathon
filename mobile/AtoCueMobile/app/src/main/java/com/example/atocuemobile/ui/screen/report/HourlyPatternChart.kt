package com.example.atocuemobile.ui.screen.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// TODO: 시간대별 값 데이터 모델
data class HourlyData(
    val hour: Int,     // 예: 12, 13, 14...
    val value: Float   // 그래프 막대 높이에 쓰일 값
)

@Composable
fun HourlyPatternChart(
    data: List<HourlyData>
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text("저녁 8시부터 10시 사이에 집중적으로 발생했어요.")
        // TODO: 실제 막대그래프. Canvas로 직접 그리거나 외부 라이브러리 없이
        // 간단히 Row + Box(height 비율)로 구현 가능. 값 정해지면 구체적으로 짜줄게.
        Column(modifier = Modifier.fillMaxWidth().height(150.dp)) {
            // 그래프 영역 placeholder
        }
    }
}