package com.example.atocuemobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.atocuemobile.ui.screen.record.meal.MealCaptureScreen
import com.example.atocuemobile.ui.theme.AtoCueMobileTheme
import com.example.atocuemobile.ui.screen.record.meal.MealRecordInputScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AtoCueMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showNextScreen by remember { mutableStateOf(false) }

                    if (!showNextScreen) {
                        MealCaptureScreen(
                            onCapturedComplete = {
                                showNextScreen = true
                            },
                            onBack = {
                                finish()
                            }
                        )
                    } else {
                        MealRecordInputScreen(
                            onBack = { showNextScreen = false}
                        ) { }

                    }
                }
            }
        }
    }
}