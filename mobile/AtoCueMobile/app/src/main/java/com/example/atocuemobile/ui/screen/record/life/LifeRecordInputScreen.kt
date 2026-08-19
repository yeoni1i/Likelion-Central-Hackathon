package com.example.atocuemobile.ui.screen.record.life

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.R
import com.example.atocuemobile.ui.screen.record.component.RecordDatePickerDialog
import com.example.atocuemobile.ui.screen.timeline.AtoCueBlue
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

// 증상 아이콘 데이터 모델
data class SymptomItem(
    val name: String,
    val iconResId: Int
)

@Composable
fun LifeRecordInputScreen(
    onBack: () -> Unit,
    onSubmitComplete: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    var isSymptomExpanded by remember { mutableStateOf(true) }
    var isShowerExpanded by remember { mutableStateOf(true) }

    var selectedSymptoms by remember { mutableStateOf(setOf<String>()) }
    var selectedShowerCount by remember { mutableStateOf("3회 이상") }
    var moisturizerCount by remember { mutableFloatStateOf(5f) }

    var specialNoteText by remember { mutableStateOf("") }

    val dateFormatter = DateTimeFormatter.ofPattern("M월 dd일 (E)")

    val containerBgColor = Color(0xFFFAFAFA)
    val borderColor = Color(0xFFEBEBEB)

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
                        text = "생활 기록",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .clickable { showDatePicker = true }
                        .padding(start = 20.dp, bottom = 16.dp),
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
            // 🌟 피그마 스펙(상단 10dp, 하단 35dp, 좌우 24dp, 높이 65dp) 완벽 적용
            Surface(
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding() // 시스템 제스처 바 패딩 자동 대응
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 10.dp,
                            bottom = 35.dp
                        )
                ) {
                    Button(
                        onClick = onSubmitComplete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AtoCueBlue),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "등록하기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. 주요 증상 카드
            item {
                SymptomExpandableCard(
                    isExpanded = isSymptomExpanded,
                    selectedSymptoms = selectedSymptoms,
                    containerBgColor = containerBgColor,
                    borderColor = borderColor,
                    onToggleClick = { isSymptomExpanded = !isSymptomExpanded },
                    onSymptomToggle = { symptom ->
                        selectedSymptoms = if (selectedSymptoms.contains(symptom)) {
                            selectedSymptoms - symptom
                        } else {
                            selectedSymptoms + symptom
                        }
                    }
                )
            }

            // 2. 샤워, 보습제 카드
            item {
                ShowerExpandableCard(
                    isExpanded = isShowerExpanded,
                    selectedShowerCount = selectedShowerCount,
                    moisturizerCount = moisturizerCount,
                    containerBgColor = containerBgColor,
                    borderColor = borderColor,
                    onToggleClick = { isShowerExpanded = !isShowerExpanded },
                    onShowerCountSelect = { selectedShowerCount = it },
                    onMoisturizerChange = { moisturizerCount = it }
                )
            }

            // 3. 특이사항 기록
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "특이사항 기록",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = specialNoteText,
                        onValueChange = { specialNoteText = it },
                        placeholder = {
                            Text(
                                text = "이전과 달라진 점, 하루 일과 중 특이했던 사항이 있다면 상세히 입력해주세요.\n\n예시 )\n보습제를 다써서 급하게 기존에 쓰던 제품과 다른 제품을 사용함. 제품은 00사의 0000 병원 이후 평소에 사용하던 스테로이드제가 아닌 다른 약을 처방받아 사용함\n\n또한 오늘 학교 체육시간에 땀을 흘리며 피구를 해서 목 부위 땀띠가 발생",
                                fontSize = 13.sp,
                                color = Color(0xFFA0A0A0),
                                lineHeight = 18.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = containerBgColor,
                            focusedContainerColor = containerBgColor,
                            unfocusedBorderColor = borderColor,
                            focusedBorderColor = AtoCueBlue
                        )
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }

    if (showDatePicker) {
        RecordDatePickerDialog(
            initialDate = selectedDate,
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { selectedDate = it }
        )
    }
}

// 주요 증상 카드
@Composable
private fun SymptomExpandableCard(
    isExpanded: Boolean,
    selectedSymptoms: Set<String>,
    containerBgColor: Color,
    borderColor: Color,
    onToggleClick: () -> Unit,
    onSymptomToggle: (String) -> Unit
) {
    val symptomList = listOf(
        SymptomItem("심한 가려움증", R.drawable.symptom_severe_itch),
        SymptomItem("붉은 발진", R.drawable.symptom_red_rash),
        SymptomItem("긁힌 상처", R.drawable.symptom_scratch_wound),
        SymptomItem("진물과 딱지", R.drawable.symptom_oozing_scab),
        SymptomItem("피부 태선화", R.drawable.symptom_lichenification),
        SymptomItem("건조증", R.drawable.symptom_dryness)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerBgColor, RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "주요 증상",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.Black
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                symptomList.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { symptomItem ->
                            val isSelected = selectedSymptoms.contains(symptomItem.name)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(95.dp)
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) AtoCueBlue else Color(0xFFEEEEEE),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onSymptomToggle(symptomItem.name) }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = symptomItem.iconResId),
                                        contentDescription = symptomItem.name,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = symptomItem.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 샤워, 보습제 선택 카드
@Composable
private fun ShowerExpandableCard(
    isExpanded: Boolean,
    selectedShowerCount: String,
    moisturizerCount: Float,
    containerBgColor: Color,
    borderColor: Color,
    onToggleClick: () -> Unit,
    onShowerCountSelect: (String) -> Unit,
    onMoisturizerChange: (Float) -> Unit
) {
    val showerOptions = listOf("1회", "2회", "3회 이상")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerBgColor, RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "샤워, 보습제",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.Black
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = "샤워 횟수",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    showerOptions.forEach { option ->
                        val isSelected = option == selectedShowerCount
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .background(
                                    color = if (isSelected) AtoCueBlue else Color.White,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) AtoCueBlue else Color(0xFFEEEEEE),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onShowerCountSelect(option) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "보습제 사용횟수",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(AtoCueBlue, RoundedCornerShape(8.dp))
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${moisturizerCount.roundToInt()}회",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                CustomMoisturizerSlider(
                    value = moisturizerCount,
                    onValueChange = onMoisturizerChange,
                    valueRange = 1f..20f
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "1회", fontSize = 11.sp, color = Color.Gray)
                    Text(text = "10회", fontSize = 11.sp, color = Color.Gray)
                    Text(text = "20회", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}

// 슬라이더 컴포넌트
@Composable
private fun CustomMoisturizerSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 1f..20f
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(vertical = 10.dp)
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val thumbSizePx = with(LocalDensity.current) { 36.dp.toPx() }
        val maxOffset = (widthPx - thumbSizePx).coerceAtLeast(1f)

        val fraction = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
        val currentOffsetPx = fraction * maxOffset

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val newFraction = (offset.x / widthPx).coerceIn(0f, 1f)
                        val newValue = valueRange.start + newFraction * (valueRange.endInclusive - valueRange.start)
                        onValueChange(newValue)
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val newFraction = (change.position.x / widthPx).coerceIn(0f, 1f)
                        val newValue = valueRange.start + newFraction * (valueRange.endInclusive - valueRange.start)
                        onValueChange(newValue)
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color(0xFFEBEBEB), CircleShape)
            )

            Box(
                modifier = Modifier
                    .width(with(LocalDensity.current) { (currentOffsetPx + thumbSizePx / 2).toDp() })
                    .height(8.dp)
                    .background(AtoCueBlue, CircleShape)
            )

            Box(
                modifier = Modifier
                    .offset { IntOffset(currentOffsetPx.roundToInt(), 0) }
                    .size(36.dp)
                    .shadow(3.dp, CircleShape)
                    .background(Color.White, CircleShape)
                    .border(1.5.dp, Color(0xFFE5E5E5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(12.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}