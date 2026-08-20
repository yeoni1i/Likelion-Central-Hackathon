package com.example.atocuemobile.ui.screen.report.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.network.dto.TriggerFactorDto
import androidx.compose.ui.unit.em

val AtoCueBlue = Color(0xFF3B82F6)

private val tightTextStyle = TextStyle(
    platformStyle = PlatformTextStyle(
        includeFontPadding = false
    )
)

@Composable
fun DailyReportHeaderSection(
    dateText: String,
    comparisonText: String,
    summaryTitle: String,
    triggerFactors: List<TriggerFactorDto> = emptyList(),
    onPrevDateClick: () -> Unit = {},
    onNextDateClick: () -> Unit = {}
) {
    val headerGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF6F7FB),
            Color(0xFFE0EAFF),
            Color(0xFFF6F7FB)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = headerGradient)
            .padding(top = 16.dp)
    ) {
        // 1. 날짜 선택 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevDateClick) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "이전 날짜"
                )
            }

            Text(
                text = dateText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                style = tightTextStyle,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(onClick = onNextDateClick) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "다음 날짜"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. 메인 요약 영역
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = comparisonText,
                fontSize = 15.sp,
                color = Color(0xFF5D6068),
                style = tightTextStyle,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = summaryTitle,
                fontSize = 21.sp,
                lineHeight = 29.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111318),
                style = tightTextStyle,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (triggerFactors.isNotEmpty()) {
            Spacer(modifier = Modifier.height(34.dp))

            // 3. AI 자극 요인 카드
            LazyRow(
                contentPadding = PaddingValues(
                    start = 28.dp,
                    end = 28.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = triggerFactors,
                    key = { trigger ->
                        "${trigger.rank}-${trigger.type}-${trigger.factor}"
                    }
                ) { trigger ->
                    TriggerFactorCard(trigger = trigger)
                }
            }

            Spacer(modifier = Modifier.height(34.dp))
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 4. 다음 섹션과 이어지는 흰색 곡선
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(25.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 25.dp,
                        topEnd = 25.dp
                    )
                )
                .background(Color.White)
        )
    }
}

@Composable
private fun TriggerFactorCard(
    trigger: TriggerFactorDto
) {
    val categoryLabel = when (trigger.type) {
        "FOOD" -> "식단 기록"
        "LIFESTYLE" -> "생활 기록"
        "ENVIRONMENT" -> "환경 기록"
        else -> "최근 기록"
    }

    val categoryDescription = when (trigger.type) {
        "FOOD" -> "반복해서 함께 관찰된 식단 요인이에요"
        "LIFESTYLE" -> "생활 패턴 변화와 함께 관찰됐어요"
        "ENVIRONMENT" -> "환경 변화와 함께 관찰된 요인이에요"
        else -> "최근 기록에서 함께 관찰된 요인이에요"
    }

    Box(
        modifier = Modifier
            .width(258.dp)
            .height(292.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(22.dp),
                clip = false
            )
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 22.dp,
                    end = 22.dp,
                    top = 22.dp,
                    bottom = 20.dp
                )
        ) {
            Text(
                text = "${trigger.rank}순위",
                fontSize = 14.sp,
                color = AtoCueBlue,
                fontWeight = FontWeight.Bold,
                style = tightTextStyle
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = trigger.factor,
                fontSize = 19.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111318),
                style = tightTextStyle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = trigger.reason,
                fontSize = 14.sp,
                color = Color(0xFF84878E),
                lineHeight = 1.5.em,
                style = tightTextStyle,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFEAF2FF))
                    .padding(
                        horizontal = 14.dp,
                        vertical = 13.dp
                    )
            ) {
                Text(
                    text = categoryLabel,
                    fontSize = 11.sp,
                    color = AtoCueBlue,
                    fontWeight = FontWeight.Bold,
                    style = tightTextStyle
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = categoryDescription,
                    fontSize = 11.sp,
                    color = Color(0xFF3977D7),
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 16.sp,
                    style = tightTextStyle
                )
            }
        }
    }
}