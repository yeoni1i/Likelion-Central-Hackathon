package com.example.atocuemobile.ui.screen.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// TODO: 카드 데이터 모델. value/unit은 습도(%), 온도(°C) 등 상황에 맞게 확장
data class ReportCauseCard(
    val rank: Int,        // 예: 1위, 2위
    val label: String,     // 예: "건조한 환경"
    val value: String,     // 예: "35%"
    val changeLabel: String // 예: "▼ -6"
)

@Composable
fun ReportSummarySection(
    title: String,
    cause: String,
    changeRate: String,
    // TODO: 실제로는 카드 리스트를 파라미터로 받아서 forEach로 렌더링
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(text = title)      // TODO: 스타일(폰트 크기/굵기) 피그마 참고해서 적용
        Text(text = cause)
        Text(text = changeRate)

        Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            // TODO: ReportCauseCard 2개 나란히 배치 (Card 컴포저블 사용)
            Card(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("1위")
                    Text("건조한 환경")
                    Text("35%")
                }
            }
            Card(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("2위")
                    Text("온도")
                    Text("30°C")
                }
            }
        }
    }
}