package com.example.atocuemobile

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import com.example.atocuemobile.ui.screen.record.life.LifeRecordInputScreen
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import com.google.android.gms.location.LocationServices
import com.example.atocuemobile.ui.screen.report.DailyReportScreen
import android.content.Context
import androidx.core.content.edit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //installSplashScreen()
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

    // 식단 캡처 화면 → 입력 화면으로 넘길 사진 Uri
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

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
            onLoginSuccess = { userId, token, isOnboarded, childId ->

                Log.d(
                    "AtoCue_Auth",
                    "로그인 성공 userId=$userId, " +
                            "isOnboarded=$isOnboarded, childId=$childId"
                )

                loggedInUserId = userId
                authToken = token
                RetrofitClient.accessToken = token

                if (!isOnboarded) {

                    // 신규 사용자
                    OnboardingPrefs.clear(context, userId)
                    savedChildId = null

                    currentScreen = AppScreen.PERMISSION

                } else if (childId != null) {

                    // 기존 온보딩 완료 사용자
                    savedChildId = childId

                    // 로컬 저장은 판단 기준이 아니라 캐시 용도
                    OnboardingPrefs.setCompleted(
                        context = context,
                        userId = userId,
                        childId = childId
                    )

                    currentScreen = AppScreen.MAIN

                } else {

                    // DB 상태가 비정상적인 경우
                    Log.e(
                        "AtoCue_Auth",
                        "isOnboarded=true인데 childId=null"
                    )

                    savedChildId = null
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

                        Log.d(
                            "AtoCue_Onboarding",
                            "온보딩 성공: userId=$userId, childId=$childId"
                        )

                        if (userId != null && childId != null) {
                            savedChildId = childId

                            OnboardingPrefs.setCompleted(
                                context = context,
                                userId = userId,
                                childId = childId
                            )

                            val saved =
                                OnboardingPrefs.getCompletedChildId(context, userId)

                            Log.d(
                                "AtoCue_Onboarding",
                                "온보딩 저장 완료: userId=$userId, childId=$saved"
                            )
                        } else {
                            Log.e(
                                "AtoCue_Onboarding",
                                "온보딩 저장 실패: userId 또는 childId가 null입니다. userId=$userId, childId=$childId"
                            )
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
                    userId = loggedInUserId!!,
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
                    onCapturedComplete = { uri ->
                        capturedImageUri = uri
                        showNextScreen = true
                    },
                    onBack = {
                        currentScreen = AppScreen.MAIN
                    }
                )
            } else {
                MealRecordInputScreen(
                    imageUri = capturedImageUri,
                    onBack = { showNextScreen = false },
                    onSubmitComplete = {
                        capturedImageUri = null
                        showNextScreen = false
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
    userId: Long,
    homeViewModel: HomeViewModel,
    parentName: String,
    onNavigateToConnectWatch: () -> Unit,
    onNavigateToMealCapture: () -> Unit,
    onNavigateToLifeRecord: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val uiState by homeViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(BottomNavTab.HOME) }

    LaunchedEffect(Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val hasFineLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocation || hasCoarseLocation) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        homeViewModel.fetchWeather(
                            lat = location.latitude,
                            lon = location.longitude
                        )
                    } else {
                        homeViewModel.fetchWeather(lat = 37.5665, lon = 126.9780)
                    }
                }.addOnFailureListener {
                    homeViewModel.fetchWeather(lat = 37.5665, lon = 126.9780)
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                homeViewModel.fetchWeather(lat = 37.5665, lon = 126.9780)
            }
        } else {
            homeViewModel.fetchWeather(lat = 37.5665, lon = 126.9780)
        }

        homeViewModel.loadToday()
    }

    when (selectedTab) {

        BottomNavTab.HOME -> {

            HomeScreen(
                isDeviceConnected = uiState.isDeviceConnected,

                detectionState =
                    if (uiState.isDeviceConnected) {
                        if (uiState.isDetecting) {
                            DetectionState.DETECTING
                        } else {
                            DetectionState.READY
                        }
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
                },

                onLifeLogClick = {
                    onNavigateToLifeRecord()
                }
            )
        }


        BottomNavTab.TIMELINE -> {
            TimelineScreen(
                homeViewModel = homeViewModel,

                selectedBottomTab = selectedTab,

                onBottomTabSelected = { tab ->
                    selectedTab = tab
                },

                onAddRecordClick = {
                    onNavigateToMealCapture()
                },

                onNavigateToLifeRecordInput = {
                    onNavigateToLifeRecord()
                }
            )
        }

        BottomNavTab.REPORT -> {
            DailyReportScreen(
                userId = userId,
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
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
    }
}