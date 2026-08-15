package com.example.atocuemobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.atocuemobile.ui.component.BottomNavTab
import com.example.atocuemobile.ui.screen.HomeScreen
import com.example.atocuemobile.ui.screen.LoginScreen
import com.example.atocuemobile.ui.screen.SignUpScreen
import com.example.atocuemobile.ui.theme.AtoCueMobileTheme
import com.example.atocuemobile.viewmodel.HomeViewModel
import com.example.atocuemobile.ui.screen.DetectionState
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AtoCueMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRoot()
                }
            }
        }
    }
}

private enum class AppScreen {
    LOGIN,
    SIGN_UP,
    MAIN
}

@Composable
private fun AppRoot() {
    var currentScreen by remember { mutableStateOf(AppScreen.LOGIN) }
    var loggedInUserId by remember { mutableStateOf<Long?>(null) }
    var authToken by remember { mutableStateOf<String?>(null) }

    when (currentScreen) {
        AppScreen.LOGIN -> LoginScreen(
            onLoginSuccess = { userId, token ->
                loggedInUserId = userId
                authToken = token
                currentScreen = AppScreen.MAIN
            },
            onNavigateToSignUp = {
                currentScreen = AppScreen.SIGN_UP
            }
        )

        AppScreen.SIGN_UP -> SignUpScreen(
            onSignUpSuccess = {
                currentScreen = AppScreen.LOGIN
            },
            onNavigateBack = {
                currentScreen = AppScreen.LOGIN
            }
        )

        AppScreen.MAIN -> {
            val currentUserId = loggedInUserId ?: 1L
            MainHomeScreenContainer(
                userId = currentUserId,
                token = authToken
            )
        }
    }
}

@Composable
private fun MainHomeScreenContainer(
    userId: Long,
    token: String?
) {
    val homeViewModel: HomeViewModel = remember(userId) {
        HomeViewModel(userId = userId, initialDeviceConnected = true)
    }

    val uiState by homeViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(BottomNavTab.HOME) }

    LaunchedEffect(userId) {
        homeViewModel.fetchWeather(token = token)
        homeViewModel.loadToday()
    }

    HomeScreen(
        isDeviceConnected = uiState.isDeviceConnected,
        detectionState = if (uiState.isDetecting) DetectionState.DETECTING else DetectionState.READY,
        currentStatus = uiState.currentStatus,
        totalScratchSeconds = uiState.totalScratchSecondsToday,
        weatherData = uiState.weatherData,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        onRefreshClick = {
            homeViewModel.loadToday()
            homeViewModel.fetchWeather(token = token)
        },
        onStartDetectionClick = {
            homeViewModel.onStartDetection()
        },
        onStopDetectionClick = {
            homeViewModel.onStopDetection()
        }
    )
}