package com.example.atocuemobile.ui.screen.report.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.screen.report.model.ReportSummaryCard

@Composable
fun SummaryCardRow(cards: List<ReportSummaryCard>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        cards.forEach { card -> SummaryCardItem(card) }
    }
}

@Composable
private fun SummaryCardItem(card: ReportSummaryCard) {
    Column(
        modifier = Modifier
            .width(220.dp)               // TODO: 정확한 카드 너비는 피그마 값으로 교체
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5F5F7)) // TODO: CardBackground 등 공용 색상으로 교체
            .padding(16.dp)
    ) {
        Text(text = "${card.rank}순위", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = card.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = card.description, fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = card.metricLabel, fontSize = 12.sp, color = Color.Gray)
                Text(text = card.metricValue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (card.metricChangeIsBad) Color(0xFFFFEBEE) else Color(0xFFE3F2FD)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = card.metricChangeLabel,
                    fontSize = 12.sp,
                    color = if (card.metricChangeIsBad) Color(0xFFE53935) else Color(0xFF5398FF)
                )
            }
        }

        if (card.quote != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .padding(10.dp)
            ) {
                Column {
                    Text(text = "\" ${card.quote} \"", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "오늘 | 특이사항 기록", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
    }
}