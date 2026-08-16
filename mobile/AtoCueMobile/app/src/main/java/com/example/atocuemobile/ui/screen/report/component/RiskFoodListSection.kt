package com.example.atocuemobile.ui.screen.report.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.screen.report.model.RiskFoodItem
import java.time.LocalDate

private const val ITEMS_PER_PAGE = 4  // 사진4 안내: "4개 이상일 경우 페이지 변경 버튼 추가"

@Composable
fun RiskFoodListSection(
    items: List<RiskFoodItem>,
    baseDate: LocalDate
) {
    var page by remember { mutableStateOf(0) }
    val totalPages = ((items.size - 1) / ITEMS_PER_PAGE) + 1

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Text(text = "위험 식단 리스트", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = riskFoodSummary(items),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "${baseDate.monthValue}월 ${baseDate.dayOfMonth}일 기준", fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        val pageItems = items.drop(page * ITEMS_PER_PAGE).take(ITEMS_PER_PAGE)
        pageItems.forEach { item ->
            RiskFoodRow(item)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (totalPages > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (page > 0) page-- },
                    enabled = page > 0
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "이전 페이지")
                }
                Text(text = "${page + 1}/$totalPages", fontSize = 13.sp)
                IconButton(
                    onClick = { if (page < totalPages - 1) page++ },
                    enabled = page < totalPages - 1
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "다음 페이지")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "*AI 리포트는 아이의 상태를 기록하고 관리하는 데 도움을 주는 보조 자료이며, " +
                    "전문의 진단이나 전문적인 소견을 대신하지 않습니다.\n\n" +
                    "*리포트 내용을 바탕으로 의료진에게 질문하거나 진료 시 참고할 수 있지만, " +
                    "증상이 지속되거나 악화될 경우 반드시 전문의와 상담해주세요.",
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun RiskFoodRow(item: RiskFoodItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF5398FF)) // TODO: 반응 횟수 많을수록 진하게 하려면 alpha 계산 로직 추가
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(text = "누적 반응 ${item.reactionCount}회", fontSize = 13.sp, color = Color.White)
        }
        Text(text = item.foodNames, fontSize = 13.sp)
    }
}

private fun riskFoodSummary(items: List<RiskFoodItem>): String {
    // TODO: 실제로는 반응 횟수 1위 항목 기준으로 동적 생성
    val top = items.maxByOrNull { it.reactionCount }
    return if (top != null) "${top.foodNames}을\n유의하는걸 추천해요" else "특이 식단 반응이 없어요"
}