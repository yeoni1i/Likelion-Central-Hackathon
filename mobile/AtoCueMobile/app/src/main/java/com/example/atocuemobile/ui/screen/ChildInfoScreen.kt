package com.example.atocuemobile.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atocuemobile.ui.component.OnboardingProgressBar
import com.example.atocuemobile.ui.component.PrimaryButton
import com.example.atocuemobile.ui.theme.AtoCueMobileTheme
import com.example.atocuemobile.ui.theme.BorderGray
import com.example.atocuemobile.ui.theme.LabelGray
import com.example.atocuemobile.ui.theme.Pretendard
import com.example.atocuemobile.ui.theme.PrimaryBlue
import com.example.atocuemobile.ui.theme.TitleBlack
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildInfoScreen(
    onNext: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var selectedYear by remember { mutableIntStateOf(2019) }
    var selectedMonth by remember { mutableIntStateOf(7) }
    var selectedDay by remember { mutableIntStateOf(13) }
    var isDatePicked by remember { mutableStateOf(false) }

    var heightInput by remember { mutableStateOf("") }
    var weightInput by remember { mutableStateOf("") }

    var showDatePickerSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val birthText = if (isDatePicked) {
        "${selectedYear}.${selectedMonth.toString().padStart(2, '0')}.${selectedDay.toString().padStart(2, '0')}"
    } else ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
            }
        }

        OnboardingProgressBar(step = 2, totalSteps = 5)

        Spacer(modifier = Modifier.height(36.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
        ) {
            Text(
                text = "아이의 정보를 알려주세요",
                style = TextStyle(
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    lineHeight = 33.sp,
                    color = TitleBlack
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "리포트 분석에 필요해요! 외부에 공개되지 않아요",
                style = TextStyle(
                    fontFamily = Pretendard,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = TitleBlack
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            ChildInputRow(label = "이름") {
                ChildInputField(
                    value = nameInput,
                    onValueChange = { nameInput = it }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            ChildInputRow(label = "생년월일") {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(52.dp)
                        .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
                        .clickable { showDatePickerSheet = true }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = birthText,
                        style = TextStyle(fontSize = 14.sp, color = TitleBlack)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            ChildInputRow(label = "키") {
                ChildInputField(
                    value = heightInput,
                    onValueChange = { heightInput = it },
                    unit = "Cm",
                    keyboardType = KeyboardType.Number
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            ChildInputRow(label = "몸무게") {
                ChildInputField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    unit = "Kg",
                    keyboardType = KeyboardType.Number
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "다음",
                enabled = true,
                onClick = onNext
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDatePickerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showDatePickerSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            DatePickerBottomSheetContent(
                initialYear = selectedYear,
                initialMonth = selectedMonth,
                initialDay = selectedDay,
                onCancel = { showDatePickerSheet = false },
                onConfirm = { year, month, day ->
                    selectedYear = year
                    selectedMonth = month
                    selectedDay = day
                    isDatePicked = true
                    showDatePickerSheet = false
                }
            )
        }
    }
}

@Composable
private fun ChildInputRow(
    label: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = Pretendard,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                color = TitleBlack
            )
        )
        content()
    }
}

@Composable
private fun ChildInputField(
    value: String,
    onValueChange: (String) -> Unit,
    unit: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(52.dp)
            .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                textStyle = TextStyle(fontSize = 15.sp, color = TitleBlack),
                modifier = Modifier.weight(1f)
            )
            if (unit != null) {
                Text(
                    text = unit,
                    style = TextStyle(fontSize = 15.sp, color = LabelGray)
                )
            }
        }
    }
}

@Composable
private fun DatePickerBottomSheetContent(
    initialYear: Int,
    initialMonth: Int,
    initialDay: Int,
    onCancel: () -> Unit,
    onConfirm: (year: Int, month: Int, day: Int) -> Unit
) {
    var year by remember { mutableIntStateOf(initialYear) }
    var month by remember { mutableIntStateOf(initialMonth) }
    var day by remember { mutableIntStateOf(initialDay) }

    val yearList = remember { (2010..2026).toList() }
    val monthList = remember { (1..12).toList() }
    val dayList = remember { (1..31).toList() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(modifier = Modifier.width(90.dp), contentAlignment = Alignment.Center) {
                Text("년도", style = TextStyle(fontSize = 14.sp, color = LabelGray))
            }
            Box(modifier = Modifier.width(90.dp), contentAlignment = Alignment.Center) {
                Text("월", style = TextStyle(fontSize = 14.sp, color = LabelGray))
            }
            Box(modifier = Modifier.width(90.dp), contentAlignment = Alignment.Center) {
                Text("일", style = TextStyle(fontSize = 14.sp, color = LabelGray))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WheelPickerColumn(
                items = yearList,
                initialItem = year,
                onItemSelected = { year = it }
            )
            WheelPickerColumn(
                items = monthList,
                initialItem = month,
                onItemSelected = { month = it }
            )
            WheelPickerColumn(
                items = dayList,
                initialItem = day,
                onItemSelected = { day = it }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEBF2FF)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text("취소", color = PrimaryBlue, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = { onConfirm(year, month, day) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text("확인", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WheelPickerColumn(
    items: List<Int>,
    initialItem: Int,
    onItemSelected: (Int) -> Unit
) {
    val itemHeight = 36.dp
    val visibleItemsCount = 5
    val initialIndex = remember { items.indexOf(initialItem).coerceAtLeast(0) }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val coroutineScope = rememberCoroutineScope()

    var selectedIndex by remember { mutableIntStateOf(initialIndex) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .filter { !it }
            .collectLatest {
                val index = listState.firstVisibleItemIndex
                if (index in items.indices) {
                    selectedIndex = index
                    onItemSelected(items[index])
                }
            }
    }

    Box(
        modifier = Modifier
            .width(90.dp)
            .height(itemHeight * visibleItemsCount),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = snapFlingBehavior,
            contentPadding = PaddingValues(vertical = itemHeight * 2),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items.size) { index ->
                val item = items[index]
                val isSelected = index == selectedIndex

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .clickable {
                            selectedIndex = index
                            onItemSelected(items[index])
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .height(16.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.width(20.dp))
                        }

                        Text(
                            text = item.toString().padStart(2, '0'),
                            style = TextStyle(
                                fontFamily = Pretendard,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 27.sp,
                                color = if (isSelected) Color(0xFF000000) else Color(0xFF000000).copy(alpha = 0.3f),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight * 1.5f)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, Color.White.copy(alpha = 0f))
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight * 1.5f)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0f), Color.White)
                    )
                )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChildInfoScreenPreview() {
    AtoCueMobileTheme {
        ChildInfoScreen(
            onNext = {},
            onNavigateBack = {}
        )
    }
}