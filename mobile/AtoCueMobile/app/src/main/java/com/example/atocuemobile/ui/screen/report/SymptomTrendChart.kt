package com.example.atocuemobile.ui.screen.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// TODO: 요일별 증상 값 데이터 모델
data class SymptomTrendData(
    val dayLabel: String,  // 예: "월", "화", "수"...
    val value: Float
)

@Composable
fun SymptomTrendChart(
    data: List<SymptomTrendData>
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text("최근 기록보다 증상이 심해졌어요")
        // TODO: 그래프 영역. HourlyPatternChart랑 비슷한 방식으로 구현
        Column(modifier = Modifier.fillMaxWidth().height(150.dp)) {
            // placeholder
        }
        Text("최근 일일 증상은 가벼웠어요.")
        Text("최근 발작 빈도는 18% 증가했어요.")
    }
}