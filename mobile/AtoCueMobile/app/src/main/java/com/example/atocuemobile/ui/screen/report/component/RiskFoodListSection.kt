package com.example.atocuemobile.ui.screen.report.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class RiskFoodItem(val count: Int, val name: String)

@Composable
fun RiskFoodListSection(
    baseDateText: String = "0월 00일 기준",
    foodList: List<RiskFoodItem> = listOf(
        RiskFoodItem(5, "유제품, 밀가루"),
        RiskFoodItem(4, "00식품"),
        RiskFoodItem(3, "00식품"),
        RiskFoodItem(2, "00식품"),
        RiskFoodItem(1, "00식품")
    )
) {
    val sortedList = remember(foodList) { foodList.sortedByDescending { it.count } }

    var currentPage by remember { mutableStateOf(1) }
    val itemsPerPage = 4
    val totalPages = (sortedList.size + itemsPerPage - 1) / itemsPerPage

    val displayedItems = remember(currentPage, sortedList) {
        val start = (currentPage - 1) * itemsPerPage
        val end = (start + itemsPerPage).coerceAtMost(sortedList.size)
        if (start < sortedList.size) sortedList.subList(start, end) else emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 20.dp) // 좌우 24dp 패딩
    ) {
        Text("위험 식단 리스트", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text("유제품과 밀가루 제품을\n유의하는걸 추천해요", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))
        Text(baseDateText, fontSize = 13.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            displayedItems.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (currentPage == 1 && index == 0) AtoCueBlue else Color(0xFFEBF3FF))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "누적 반응 ${item.count}회",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentPage == 1 && index == 0) Color.White else AtoCueBlue
                        )
                    }

                    Text(
                        text = item.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (totalPages > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (currentPage > 1) currentPage-- },
                    enabled = currentPage > 1
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "이전 페이지",
                        tint = if (currentPage > 1) Color.Black else Color.LightGray
                    )
                }

                Text(
                    text = "$currentPage / $totalPages",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                IconButton(
                    onClick = { if (currentPage < totalPages) currentPage++ },
                    enabled = currentPage < totalPages
                ) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "다음 페이지",
                        tint = if (currentPage < totalPages) Color.Black else Color.LightGray
                    )
                }
            }
        }
    }
}