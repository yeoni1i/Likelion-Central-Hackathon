package com.example.atocuemobile.ui.screen.timeline.meal.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.atocuemobile.ui.screen.timeline.AtoCueBlue
import com.example.atocuemobile.ui.screen.timeline.model.MealRecord

@Composable
fun MealRecordCard(
    record: MealRecord,
    onClick: () -> Unit
) {

    val hasRecord =
        !record.photoUrl.isNullOrBlank() ||
                record.menuItems.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.15.dp))
            .background(Color(0xFFFAFAFA))
            .border(
                1.dp,
                color = Color(0xFFEBEBEB),
                shape = RoundedCornerShape(10.15.dp)
            )
            .clickable { onClick() }
    ) {

        when {
            !record.photoUrl.isNullOrBlank() -> {

                AsyncImage(
                    model = record.photoUrl,
                    contentDescription = "식단 사진",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            record.menuItems.isNotEmpty() -> {

                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp
                        )
                ) {

                    record.menuItems
                        .take(4)
                        .forEach { food ->

                            Text(
                                text = food,
                                fontSize = 13.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )
                        }
                }
            }

            else -> {

                Text(
                    text = "아직 등록된\n기록이 없습니다",
                    fontSize = 13.sp,
                    color = Color(0xFF6C6E72),
                    fontWeight = FontWeight(500),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(
                            start = 16.dp,
                            top = 19.dp
                        )
                )
            }
        }

        Text(
            text = record.mealType.label,
            fontSize = 12.sp,
            fontWeight = FontWeight(500),
            color = Color(0xFF2367CE),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = 12.dp,
                    end = 12.dp
                )
                .background(
                    color = AtoCueBlue.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 6.dp
                )
        )
    }
}