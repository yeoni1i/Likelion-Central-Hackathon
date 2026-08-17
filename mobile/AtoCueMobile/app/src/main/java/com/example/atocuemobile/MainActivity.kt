package com.example.atocuemobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.atocuemobile.ui.screen.timeline.TimelineScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // TimelineScreen.kt에 선언된 화면을 불러와 실행만 함
            TimelineScreen(
                onAddRecordClick = {
                    // 식단 기록 추가 버튼 클릭 시 동작할 로직
                },
                onNavigateToLifeRecordInput = {
                    // 생활 기록 입력 이동 버튼 클릭 시 동작할 로직
                }
            )
        }
    }
}