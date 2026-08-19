package com.example.atocuemobile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import com.example.atocuemobile.ui.screen.record.life.LifeRecordInputScreen
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
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
import com.example.atocuemobile.ui.screen.record.meal.MealCaptureScreen
import com.example.atocuemobile.ui.screen.record.meal.MealRecordInputScreen
import com.example.atocuemobile.ui.viewmodel.OnboardingViewModel
import com.example.atocuemobile.viewmodel.HomeViewModel
import com.example.atocuemobile.ui.screen.timeline.TimelineScreen

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

// 온보딩 완료 여부를 로컬에 기억해두는 헬퍼
private object OnboardingPrefs {
    private const val PREFS_NAME = "atocue_onboarding_prefs"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getCompletedChildId(context: Context, userId: Long): Long? {
        val value = prefs(context).getLong("child_id_$userId", -1L)
        return if (value == -1L) null else value
    }

    fun setCompleted(context: Context, userId: Long, childId: Long) {
        prefs(context).edit {
            putLong("child_id_$userId", childId)
        }
    }

    fun clear(context: Context, userId: Long) {
        prefs(context).edit {
            remove("child_id_$userId")
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
    CONNECT_WATCH,
    MEAL_CAPTURE,

    LIFE_RECORD
}

@Composable
private fun AppRoot() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(AppScreen.LOGIN) }
    var loggedInUserId by remember { mutableStateOf<Long?>(null) }
    var authToken by remember { mutableStateOf<String?>(null) }

    var savedChildId by remember { mutableStateOf<Long?>(null) }

    val onboardingViewModel: OnboardingViewModel = viewModel()
    val registeredChildId = onboardingViewModel.registeredChildId

    val effectiveChildId = registeredChildId ?: savedChildId

    val homeViewModel: HomeViewModel? =
        if (loggedInUserId != null && effectiveChildId != null) {
            remember(loggedInUserId, effectiveChildId) {
                HomeViewModel(
                    userId = loggedInUserId!!,
                    childId = effectiveChildId,
                    initialDeviceConnected = false
                )
            }
        } else {
            null
        }

    when (currentScreen) {
        AppScreen.LOGIN -> LoginScreen(
            onLoginSuccess = { userId, token ->
                Log.d("AtoCue_Auth", "====== [로그인 성공] userId: $userId, token: $token ======")
                loggedInUserId = userId
                authToken = token
                RetrofitClient.accessToken = token

                val existingChildId = OnboardingPrefs.getCompletedChildId(context, userId)
                if (existingChildId != null) {
                    savedChildId = existingChildId
                    currentScreen = AppScreen.MAIN
                } else {
                    currentScreen = AppScreen.PERMISSION
                }
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
                        val userId = loggedInUserId
                        val childId = onboardingViewModel.registeredChildId
                        if (userId != null && childId != null) {
                            OnboardingPrefs.setCompleted(context, userId, childId)
                        }
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
            val vm = homeViewModel

            if (vm == null) {
                Toast.makeText(
                    context,
                    "사용자 또는 아이 정보가 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()

                currentScreen = AppScreen.LOGIN
            } else {
                val parentName = onboardingViewModel.parentName.ifBlank { "보호자" }

                MainHomeScreenContainer(
                    homeViewModel = vm,
                    parentName = parentName,

                    onNavigateToConnectWatch = {
                        currentScreen = AppScreen.CONNECT_WATCH
                    },

                    onNavigateToMealCapture = {
                        currentScreen = AppScreen.MEAL_CAPTURE
                    },

                    onNavigateToLifeRecord = {
                        currentScreen = AppScreen.LIFE_RECORD
                    },

                    onLogout = {
                        loggedInUserId = null
                        authToken = null
                        savedChildId = null
                        RetrofitClient.accessToken = null
                        currentScreen = AppScreen.LOGIN
                    }
                )
            }
        }

        AppScreen.CONNECT_WATCH -> {
            val vm = homeViewModel

            if (vm == null) {
                Toast.makeText(
                    context,
                    "워치 연결 정보를 불러올 수 없습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                currentScreen = AppScreen.MAIN
            } else {
                val uiState by vm.uiState.collectAsState()

                LaunchedEffect(uiState.isDeviceConnected) {
                    if (uiState.isDeviceConnected) {
                        currentScreen = AppScreen.MAIN
                    }
                }

                ConnectWatchScreen(
                    code = uiState.pairingCode,
                    isLoading = uiState.isLoading,
                    onRefreshCode = { vm.fetchPairingCode() },
                    onBackClick = { currentScreen = AppScreen.MAIN },
                    onSkipClick = {
                        vm.skipPairingForTest()
                        currentScreen = AppScreen.MAIN
                    }
                )
            }
        }

        AppScreen.MEAL_CAPTURE -> {
            var showNextScreen by remember { mutableStateOf(false) }

            if (!showNextScreen) {
                MealCaptureScreen(
                    onCapturedComplete = {
                        showNextScreen = true
                    },
                    onBack = {
                        currentScreen = AppScreen.MAIN
                    }
                )
            } else {
                MealRecordInputScreen(
                    onBack = { showNextScreen = false },
                    onSubmitComplete = {
                        currentScreen = AppScreen.MAIN
                    }
                )
            }
        }

        AppScreen.LIFE_RECORD -> {
            LifeRecordInputScreen(
                onBack = {
                    currentScreen = AppScreen.MAIN
                },
                onSubmitComplete = {
                    currentScreen = AppScreen.MAIN
                }
            )
        }
    }
}

@Composable
private fun MainHomeScreenContainer(
    homeViewModel: HomeViewModel,
    parentName: String,
    onNavigateToConnectWatch: () -> Unit,
    onNavigateToMealCapture: () -> Unit,
    onNavigateToLifeRecord: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(BottomNavTab.HOME) }

    LaunchedEffect(Unit) {
        homeViewModel.fetchWeather(
            lat = 37.5665,
            lon = 126.9780
        )
        homeViewModel.loadToday()
    }

    // 각 화면 컴포저블들이 자체적으로 하단바를 포함하고 있으므로,
    // 여기서는 Scaffold 없이 선택된 탭에 맞춰 컴포저블만 갈아끼워 줍니다.
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
                onTabSelected = { tab ->
                    selectedTab = tab
                },
                onConnectWatchClick = {
                    homeViewModel.fetchPairingCode()
                    onNavigateToConnectWatch()
                },
                onRefreshClick = {
                    homeViewModel.loadToday()
                    homeViewModel.fetchWeather(
                        lat = 37.5665,
                        lon = 126.9780
                    )
                },
                onStartDetectionClick = {
                    homeViewModel.onStartDetection()
                },
                onStopDetectionClick = {
                    homeViewModel.onStopDetection()
                },
                onMealLogClick = {
                    onNavigateToMealCapture()
                }
            )
        }

        BottomNavTab.TIMELINE -> {
            TimelineScreen(
                homeViewModel = homeViewModel,
                onAddRecordClick = {
                    onNavigateToMealCapture()
                },
                onNavigateToLifeRecordInput = {
                    onNavigateToLifeRecord()
                }
            )
        }

        BottomNavTab.MY -> {
            MyPageScreen(
                guardianName = parentName,
                pairingCode = uiState.pairingCode,
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                },
                onDeviceManageClick = {
                    homeViewModel.fetchPairingCode()
                    onNavigateToConnectWatch()
                },
                onLogoutClick = onLogout
            )
        }

        else -> {
            HomeScreen(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    }
}