package com.example.atocuemobile.ui.screen.record.meal

import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import coil.compose.rememberAsyncImagePainter
import com.example.atocuemobile.network.RetrofitClient
import com.example.atocuemobile.network.dto.DailyLogCreateRequest
import com.example.atocuemobile.ui.screen.record.component.RecordDatePickerDialog
import com.example.atocuemobile.ui.screen.timeline.AtoCueBlue
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MealRecordInputScreen(
    imageUri: Uri?,
    onBack: () -> Unit,
    onSubmitComplete: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    var showMealTimeSheet by remember { mutableStateOf(false) }
    var selectedMealTime by remember { mutableStateOf("식사시간") }

    var menuList by remember { mutableStateOf(listOf("", "")) }
    var isSubmitting by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val dateFormatter = DateTimeFormatter.ofPattern("M월 dd일 (E)")

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
                    IconButton(onClick = onBack) {
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
                        .clickable { showDatePicker = true }
                        .padding(start = 20.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedDate.format(dateFormatter)}, 오늘",
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
                    onClick = {
                        if (!isSubmitting) {
                            coroutineScope.launch {
                                try {
                                    isSubmitting = true
                                    Log.d("DAILY_LOG_TEST", "등록 버튼 클릭됨")

                                    val foods = menuList
                                        .map { it.trim() }
                                        .filter { it.isNotBlank() }

                                    if (foods.isEmpty()) {
                                        Toast.makeText(context, "먹은 음식을 하나 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }

                                    if (selectedMealTime == "식사시간") {
                                        Toast.makeText(context, "식사시간을 선택해주세요.", Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }

                                    val mealType = when (selectedMealTime) {
                                        "아침" -> "BREAKFAST"
                                        "점심" -> "LUNCH"
                                        "저녁" -> "DINNER"
                                        "간식" -> "SNACK"
                                        else -> null
                                    }

                                    val requestDto = DailyLogCreateRequest(
                                        mealType = mealType,
                                        foods = foods,
                                        showerCount = null,
                                        moisturizerCount = null,
                                        symptoms = emptyList(),
                                        memo = null,
                                        date = selectedDate.toString()
                                    )

                                    val json = Gson().toJson(requestDto)
                                    val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

                                    val imagePart: MultipartBody.Part? = imageUri?.let { uri ->
                                        try {
                                            val inputStream = context.contentResolver.openInputStream(uri)
                                            val file = File(context.cacheDir, "meal_${System.currentTimeMillis()}.jpg")
                                            inputStream?.use { input ->
                                                file.outputStream().use { output -> input.copyTo(output) }
                                            }
                                            val fileBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                                            MultipartBody.Part.createFormData("image", file.name, fileBody)
                                        } catch (e: Exception) {
                                            Log.e("DAILY_LOG_TEST", "이미지 파일 변환 실패", e)
                                            null
                                        }
                                    }

                                    Log.d("DAILY_LOG_TEST", "서버 전송 시작 request=$json")

                                    val response = RetrofitClient.api.createDailyLog(
                                        request = requestBody,
                                        image = imagePart
                                    )

                                    Log.d("DAILY_LOG_TEST", "식단 등록 성공 id=${response.id}")
                                    Toast.makeText(context, "식단과 사진이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
                                    onSubmitComplete()

                                } catch (e: Exception) {
                                    Log.e("DAILY_LOG_TEST", "식단 등록 실패 에러", e)
                                    Toast.makeText(context, "식단 등록 실패: ${e.message}", Toast.LENGTH_LONG).show()
                                } finally {
                                    isSubmitting = false
                                }
                            }
                        }
                    },
                    enabled = !isSubmitting,
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
                        Text(text = "등록하기", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                // 안전한 이미지 렌더링 영역 (메인 스레드 프리징 방지)
                if (imageUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
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
                        onClick = { showMealTimeSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AtoCueBlue),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = selectedMealTime, fontSize = 13.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            itemsIndexed(menuList) { index, menuText ->
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    OutlinedTextField(
                        value = menuText,
                        onValueChange = { newText ->
                            menuList = menuList.toMutableList().apply { set(index, newText) }
                        },
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
                                    menuList = menuList.toMutableList().apply { removeAt(index) }
                                }) {
                                    Icon(imageVector = Icons.Default.RemoveCircle, contentDescription = "삭제", tint = Color.LightGray)
                                }
                            }
                        }
                    )
                }
            }

            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    OutlinedButton(
                        onClick = { menuList = menuList + "" },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "메뉴 추가", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        RecordDatePickerDialog(
            initialDate = selectedDate,
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { selectedDate = it }
        )
    }

    if (showMealTimeSheet) {
        MealTimeBottomSheet(
            currentMealTime = selectedMealTime,
            onDismissRequest = { showMealTimeSheet = false },
            onMealTimeSelected = { selectedMealTime = it }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealTimeBottomSheet(
    currentMealTime: String,
    onDismissRequest: () -> Unit,
    onMealTimeSelected: (String) -> Unit
) {
    var tempSelectedTime by remember {
        mutableStateOf(if (currentMealTime == "식사시간") "아침" else currentMealTime)
    }
    val options = listOf("아침", "점심", "저녁", "간식")

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "식사시간",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기",
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { option ->
                    val isSelected = option == tempSelectedTime
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(
                                color = if (isSelected) Color(0xFFEBF3FF) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { tempSelectedTime = option }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = option,
                            fontSize = 16.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) AtoCueBlue else Color(0xFF888888)
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "선택됨",
                                tint = AtoCueBlue
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onMealTimeSelected(tempSelectedTime)
                    onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(65.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AtoCueBlue)
            ) {
                Text(
                    text = "확인",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}