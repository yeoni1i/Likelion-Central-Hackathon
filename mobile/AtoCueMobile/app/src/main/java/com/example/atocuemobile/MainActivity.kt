package com.example.atocuemobile

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atocuemobile.network.RetrofitClient
import com.example.atocuemobile.ui.component.BottomNavTab
import com.example.atocuemobile.ui.screen.ChildInfoScreen
import com.example.atocuemobile.ui.screen.ConnectWatchScreen
import com.example.atocuemobile.ui.screen.DetectionState
import com.example.atocuemobile.ui.screen.HomeScreen
import com.example.atocuemobile.ui.screen.LoginScreen
import com.example.atocuemobile.ui.screen.MyPageScreen
import com.example.atocuemobile.ui.screen.OnboardingCompleteScreen
import com.example.atocuemobile.ui.screen.ParentInfoScreen
import com.example.atocuemobile.ui.screen.PermissionScreen
import com.example.atocuemobile.ui.screen.SignUpScreen
import com.example.atocuemobile.ui.screen.SkinConditionScreen
import com.example.atocuemobile.ui.screen.SpecialNotesScreen
import com.example.atocuemobile.ui.theme.AtoCueMobileTheme
import com.example.atocuemobile.ui.viewmodel.OnboardingViewModel
import com.example.atocuemobile.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AtoCueMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
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
    PERMISSION,
    ONBOARDING_PARENT,
    ONBOARDING_CHILD,
    ONBOARDING_SKIN,
    ONBOARDING_NOTES,
    ONBOARDING_COMPLETE,
    MAIN,
    CONNECT_WATCH
}

@Composable
private fun AppRoot() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(AppScreen.LOGIN) }
    var loggedInUserId by remember { mutableStateOf<Long?>(null) }
    var authToken by remember { mutableStateOf<String?>(null) }

    val onboardingViewModel: OnboardingViewModel = viewModel()

    when (currentScreen) {
        AppScreen.LOGIN -> LoginScreen(
            onLoginSuccess = { userId, token ->
                Log.d("AtoCue_Auth", "====== [로그인 성공] userId: $userId, token: $token ======")
                loggedInUserId = userId
                authToken = token
                RetrofitClient.accessToken = token
                currentScreen = AppScreen.PERMISSION
            },
            onNavigateToSignUp = {
                currentScreen = AppScreen.SIGN_UP
            }
        )

        AppScreen.SIGN_UP -> SignUpScreen(
            onSignUpSuccess = { currentScreen = AppScreen.LOGIN },
            onNavigateBack = { currentScreen = AppScreen.LOGIN }
        )

        AppScreen.PERMISSION -> PermissionScreen(
            onConfirm = { currentScreen = AppScreen.ONBOARDING_PARENT },
            onNavigateBack = { currentScreen = AppScreen.LOGIN }
        )

        AppScreen.ONBOARDING_PARENT -> ParentInfoScreen(
            onNext = { name ->
                onboardingViewModel.parentName = name
                currentScreen = AppScreen.ONBOARDING_CHILD
            },
            onNavigateBack = { currentScreen = AppScreen.PERMISSION }
        )

        AppScreen.ONBOARDING_CHILD -> ChildInfoScreen(
            onNext = { name, birth, height, weight ->
                onboardingViewModel.childName = name
                onboardingViewModel.birthDate = birth
                onboardingViewModel.height = height
                onboardingViewModel.weight = weight
                currentScreen = AppScreen.ONBOARDING_SKIN
            },
            onNavigateBack = { currentScreen = AppScreen.ONBOARDING_PARENT }
        )

        AppScreen.ONBOARDING_SKIN -> SkinConditionScreen(
            onNext = { conditions: List<String> ->
                onboardingViewModel.selectedConditions.clear()
                onboardingViewModel.selectedConditions.addAll(conditions)
                currentScreen = AppScreen.ONBOARDING_NOTES
            },
            onNavigateBack = { currentScreen = AppScreen.ONBOARDING_CHILD }
        )

        AppScreen.ONBOARDING_NOTES -> SpecialNotesScreen(
            onNext = { notes: String ->
                onboardingViewModel.specialNote = notes
                val token = authToken ?: ""

                onboardingViewModel.submitOnboarding(
                    jwtToken = token,
                    onSuccess = {
                        currentScreen = AppScreen.ONBOARDING_COMPLETE
                    },
                    onError = { errorMsg ->
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            onNavigateBack = { currentScreen = AppScreen.ONBOARDING_SKIN }
        )

        AppScreen.ONBOARDING_COMPLETE -> OnboardingCompleteScreen(
            onConfirm = { currentScreen = AppScreen.MAIN },
            onNavigateBack = { currentScreen = AppScreen.ONBOARDING_NOTES }
        )

        AppScreen.MAIN -> {
            val currentUserId = loggedInUserId ?: 1L
            val currentChildId = 1L
            val parentName = onboardingViewModel.parentName.ifBlank { "보호자" }

            MainHomeScreenContainer(
                userId = currentUserId,
                childId = currentChildId,
                parentName = parentName,
                onNavigateToConnectWatch = {
                    currentScreen = AppScreen.CONNECT_WATCH
                },
                onLogout = {
                    loggedInUserId = null
                    authToken = null
                    RetrofitClient.accessToken = null
                    currentScreen = AppScreen.LOGIN
                }
            )
        }

        AppScreen.CONNECT_WATCH -> {
            val currentUserId = loggedInUserId ?: 1L
            val currentChildId = 1L

            val homeViewModel: HomeViewModel = remember(currentUserId, currentChildId) {
                HomeViewModel(userId = currentUserId, childId = currentChildId, initialDeviceConnected = false)
            }
            val uiState by homeViewModel.uiState.collectAsState()

            LaunchedEffect(currentUserId, currentChildId) {
                homeViewModel.fetchPairingCode()
            }

            ConnectWatchScreen(
                code = uiState.pairingCode,
                isLoading = uiState.isLoading,
                onRefreshCode = {
                    homeViewModel.fetchPairingCode()
                },
                onBackClick = {
                    currentScreen = AppScreen.MAIN
                }
            )
        }
    }
}

@Composable
private fun MainHomeScreenContainer(
    userId: Long,
    childId: Long,
    parentName: String,
    onNavigateToConnectWatch: () -> Unit,
    onLogout: () -> Unit
) {
    val homeViewModel: HomeViewModel = remember(userId, childId) {
        HomeViewModel(userId = userId, childId = childId, initialDeviceConnected = false)
    }

    val uiState by homeViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(BottomNavTab.HOME) }

    LaunchedEffect(userId, childId) {
        homeViewModel.fetchWeather(lat = 37.5665, lon = 126.9780)
        homeViewModel.fetchPairingCode()
        homeViewModel.loadToday()
    }

    when (selectedTab) {
        BottomNavTab.HOME -> {
            HomeScreen(
                isDeviceConnected = uiState.isDeviceConnected,
                detectionState = if (uiState.isDeviceConnected) {
                    if (uiState.isDetecting) DetectionState.DETECTING else DetectionState.READY
                } else {
                    DetectionState.NOT_CONNECTED
                },
                pairingCode = uiState.pairingCode,
                currentStatus = uiState.currentStatus,
                totalScratchSeconds = uiState.totalScratchSecondsToday,
                timelineItems = uiState.timelineItems,
                weatherData = uiState.weatherData,
                guideList = uiState.guideList,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onConnectWatchClick = {
                    homeViewModel.fetchPairingCode()
                    onNavigateToConnectWatch()
                },
                onRefreshClick = {
                    homeViewModel.loadToday()
                    homeViewModel.fetchWeather(lat = 37.5665, lon = 126.9780)
                    homeViewModel.fetchPairingCode()
                },
                onStartDetectionClick = {
                    homeViewModel.onStartDetection()
                },
                onStopDetectionClick = {
                    homeViewModel.onStopDetection()
                }
            )
        }

        BottomNavTab.MY -> {
            MyPageScreen(
                guardianName = parentName,
                pairingCode = uiState.pairingCode,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onDeviceManageClick = {
                    homeViewModel.fetchPairingCode()
                    onNavigateToConnectWatch()
                },
                onLogoutClick = onLogout
            )
        }

        else -> {
            HomeScreen(
                isDeviceConnected = uiState.isDeviceConnected,
                detectionState = if (uiState.isDeviceConnected) {
                    if (uiState.isDetecting) DetectionState.DETECTING else DetectionState.READY
                } else {
                    DetectionState.NOT_CONNECTED
                },
                pairingCode = uiState.pairingCode,
                weatherData = uiState.weatherData,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    }
}