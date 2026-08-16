package com.example.atocuemobile

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.atocuemobile.ui.screen.record.meal.MealCaptureScreen
import com.example.atocuemobile.ui.screen.record.meal.MealRecordInputScreen
import com.example.atocuemobile.ui.theme.AtoCueMobileTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AtoCueMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var capturedPhoto by remember { mutableStateOf<Bitmap?>(null) }
                    var showInputScreen by remember { mutableStateOf(false) }

                    if (!showInputScreen) {
                        MealCaptureScreen(
                            onCaptureComplete = { bitmap ->
                                capturedPhoto = bitmap
                                showInputScreen = true
                            },
                            onClose = { /* TODO: 나중에 실제 닫기 동작 연결 */ }
                        )
                    } else {
                        MealRecordInputScreen(
                            capturedPhoto = capturedPhoto,
                            onBackClick = { showInputScreen = false },
                            onRegisterComplete = { _, _, _ -> showInputScreen = false }
                        )
                    }
                }
            }
        }
    }
}