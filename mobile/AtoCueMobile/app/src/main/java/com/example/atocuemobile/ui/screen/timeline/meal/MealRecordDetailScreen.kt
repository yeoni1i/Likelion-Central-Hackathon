package com.example.atocuemobile.ui.screen.timeline.meal


import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.atocuemobile.network.RetrofitClient
import com.example.atocuemobile.network.dto.DailyLogCreateRequest
import com.example.atocuemobile.ui.screen.timeline.AtoCueBlue
import com.example.atocuemobile.ui.screen.timeline.model.MealRecord
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MealRecordDetailScreen(
    record: MealRecord,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val dateFormatter = DateTimeFormatter.ofPattern("M월 dd일 (E)", Locale.KOREAN)

    var menuList = remember {
        if (record.menuItems.isEmpty()) {
            mutableStateListOf("", "")
        } else {
            mutableStateListOf(*record.menuItems.toTypedArray())
        }
    }

    var selectedMealTime by remember { mutableStateOf(record.mealType.label) }
    var isSubmitting by remember { mutableStateOf(false) }

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
                        text = "식단 기록 수정",
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
                    onClick = {
                        if (!isSubmitting) {
                            coroutineScope.launch {
                                try {
                                    isSubmitting = true
                                    val validMenus = menuList.map { it.trim() }.filter { it.isNotBlank() }

                                    if (validMenus.isEmpty()) {
                                        Toast.makeText(context, "먹은 음식을 하나 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }

                                    val mealTypeEnum = when (selectedMealTime) {
                                        "아침" -> "BREAKFAST"
                                        "점심" -> "LUNCH"
                                        "저녁" -> "DINNER"
                                        "간식" -> "SNACK"
                                        else -> "BREAKFAST"
                                    }

                                    // 서버 전송용 DTO 생성
                                    val requestDto = DailyLogCreateRequest(
                                        mealType = mealTypeEnum,
                                        foods = validMenus,
                                        showerCount = null,
                                        moisturizerCount = null,
                                        symptoms = emptyList(),
                                        memo = null,
                                        date = record.date.toString()
                                    )

                                    val json = Gson().toJson(requestDto)
                                    val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

                                    // 💡 서버로 수정(PUT) API 요청 호출 (id 전달)
                                    RetrofitClient.api.updateDailyLog(
                                        id = record.id,
                                        request = requestBody,
                                        image = null
                                    )

                                    Toast.makeText(context, "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                    onBackClick()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "수정 실패: ${e.message}", Toast.LENGTH_LONG).show()
                                } finally {
                                    isSubmitting = false
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(65.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AtoCueBlue)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(text = "수정 완료", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
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

            // 2. 식사 시간 뱃지 영역
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
                        onClick = { /* 필요시 식사시간 변경 모달 연결 가능 */ },
                        colors = ButtonDefaults.buttonColors(containerColor = AtoCueBlue),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = selectedMealTime, fontSize = 13.sp, color = Color.White)
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


            itemsIndexed(menuList) { index, menuText ->
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    OutlinedTextField(
                        value = menuText,
                        onValueChange = { newText ->
                            menuList[index] = newText
                        },
                        readOnly = false,
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
                            if (menuList.size > 1) {
                                IconButton(onClick = {
                                    menuList.removeAt(index)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.RemoveCircle,
                                        contentDescription = "삭제",
                                        tint = Color.LightGray
                                    )
                                }
                            }
                        }
                    )
                }
            }

            // 4. 메뉴 추가 버튼
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    OutlinedButton(
                        onClick = { menuList.add("") },
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