package com.example.atocuemobile.ui.screen.record

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

private enum class RecordStep {
    CAPTURE,
    MEAL_EDIT,
    LIFE_RECORD
}

@Composable
fun RecordScreen(
    onFinish: () -> Unit  // 전체 기록 완료 후 어디로 이동할지 (예: 타임라인 화면)
) {
    var step by remember { mutableStateOf(RecordStep.CAPTURE) }
    var capturedPhotoUri by remember { mutableStateOf("") }

    when (step) {
        RecordStep.CAPTURE -> {
            CameraCaptureScreen(
                onClose = onFinish,
                onCaptureComplete = { uri ->
                    capturedPhotoUri = uri
                    step = RecordStep.MEAL_EDIT
                }
            )
        }
        RecordStep.MEAL_EDIT -> {
            MealEditScreen(
                photoUri = capturedPhotoUri,
                onBackClick = { step = RecordStep.CAPTURE },
                onSubmit = { _, _ ->
                    // TODO: 식단 데이터 서버 전송/저장
                    step = RecordStep.LIFE_RECORD
                }
            )
        }
        RecordStep.LIFE_RECORD -> {
            LifeRecordScreen(
                onSubmit = { _, _ ->
                    // TODO: 생활기록 데이터 서버 전송/저장
                    onFinish()
                }
            )
        }
    }
}
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true)
@Composable
fun LifeRecordScreenPreview() {
    LifeRecordScreen(onSubmit = { _, _ -> })
}