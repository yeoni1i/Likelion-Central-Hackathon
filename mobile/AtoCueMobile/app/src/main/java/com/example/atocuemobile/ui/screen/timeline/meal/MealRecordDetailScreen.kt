package com.example.atocuemobile.ui.screen.timeline.meal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.atocuemobile.ui.screen.timeline.AtoCueBlue
import com.example.atocuemobile.ui.screen.timeline.model.MealRecord
import java.time.format.DateTimeFormatter
import java.util.Locale

// MealRecordInputScreen과 동일한 디자인의 "보기 전용" 화면
// 식사시간 뱃지 / 메뉴 추가 / 등록하기 버튼은 시각적으로만 존재하며 실제 동작은 없음
@Composable
fun MealRecordDetailScreen(
    record: MealRecord,
    onBackClick: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("M월 dd일 (E)", Locale.KOREAN)

    // 메뉴가 하나도 없으면 빈 칸 2개짜리 기본 레이아웃과 모양을 맞춰줌
    val displayMenuList = if (record.menuItems.isEmpty()) {
        listOf("", "")
    } else {
        record.menuItems
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                    Text(
                        text = "식단 기록",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(start = 20.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${record.date.format(dateFormatter)}, 오늘",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "날짜변경",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { /* 보기 전용 화면이라 실제 동작 없음 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(65.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AtoCueBlue)
                ) {
                    Text(text = "등록하기", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                if (!record.photoUrl.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = record.photoUrl,
                            contentDescription = "식단 사진",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .background(Color(0xFFF2F2F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "사진이 없습니다", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "먹은음식", fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    Button(
                        onClick = { /* 보기 전용 화면이라 실제 동작 없음 */ },
                        colors = ButtonDefaults.buttonColors(containerColor = AtoCueBlue),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = record.mealType.label, fontSize = 13.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            itemsIndexed(displayMenuList) { _, menuText ->
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    OutlinedTextField(
                        value = menuText,
                        onValueChange = { /* 보기 전용 화면이라 수정 불가 */ },
                        readOnly = true,
                        placeholder = { Text("메뉴 입력", fontSize = 14.sp, color = Color.LightGray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF8F9FA),
                            focusedContainerColor = Color(0xFFF8F9FA),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = AtoCueBlue
                        ),
                        trailingIcon = {
                            IconButton(onClick = { /* 보기 전용 화면이라 실제 동작 없음 */ }) {
                                Icon(
                                    imageVector = Icons.Default.RemoveCircle,
                                    contentDescription = "삭제",
                                    tint = Color.LightGray
                                )
                            }
                        }
                    )
                }
            }

            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    OutlinedButton(
                        onClick = { /* 보기 전용 화면이라 실제 동작 없음 */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "메뉴 추가", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}