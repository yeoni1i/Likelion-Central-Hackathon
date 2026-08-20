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
import com.example.atocuemobile.ui.screen.timeline.meal.MealRecordDetailScreen
import com.example.atocuemobile.ui.screen.timeline.model.MealRecord
import com.example.atocuemobile.ui.viewmodel.OnboardingViewModel
import com.example.atocuemobile.viewmodel.HomeViewModel
import com.example.atocuemobile.ui.screen.timeline.TimelineScreen
import com.google.android.gms.location.LocationServices
import com.example.atocuemobile.ui.screen.report.DailyReportScreen
import android.content.Context
import androidx.core.content.edit

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


private object WatchDevicePrefs {
    private const val PREFS_NAME = "atocue_watch_device_prefs"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getDeviceId(context: Context, childId: Long): Long? {
        val value = prefs(context).getLong("device_id_child_$childId", -1L)
        return if (value == -1L) null else value
    }

    fun setDeviceId(context: Context, childId: Long, deviceId: Long) {
        prefs(context).edit {
            putLong("device_id_child_$childId", deviceId)
        }
    }

    fun clearDeviceId(context: Context, childId: Long) {
        prefs(context).edit {
            remove("device_id_child_$childId")
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
    TIMELINE,
    CONNECT_WATCH,
    MEAL_CAPTURE,
    MEAL_DETAIL,
    LIFE_RECORD
}

@Composable
private fun AppRoot() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(AppScreen.LOGIN) }
    var loggedInUserId by remember { mutableStateOf<Long?>(null) }
    var authToken by remember { mutableStateOf<String?>(null) }
    var savedChildId by remember { mutableStateOf<Long?>(null) }
    var savedDeviceId by remember { mutableStateOf<Long?>(null) }

    var connectWatchScreenTitle by remember { mutableStateOf("워치 연결") }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedMealRecord by remember { mutableStateOf<MealRecord?>(null) }

    val onboardingViewModel: OnboardingViewModel = viewModel()
    val registeredChildId = onboardingViewModel.registeredChildId
    val effectiveChildId = registeredChildId ?: savedChildId

    val homeViewModel: HomeViewModel? =
        if (loggedInUserId != null && effectiveChildId != null) {
            remember(loggedInUserId, effectiveChildId) {
                HomeViewModel(
                    userId = loggedInUserId!!,
                    childId = effectiveChildId,
                    initialDeviceConnected = savedDeviceId != null,
                    initialDeviceId = savedDeviceId
                )
            }
        } else {
            null
        }

    when (currentScreen) {
        AppScreen.LOGIN -> LoginScreen(
            onLoginSuccess = { userId, token, isOnboarded, childId ->
                loggedInUserId = userId
                authToken = token
                RetrofitClient.accessToken = token

                if (!isOnboarded) {
                    OnboardingPrefs.clear(context, userId)
                    savedChildId = null
                    currentScreen = AppScreen.PERMISSION
                } else if (childId != null) {
                    savedChildId = childId
                    OnboardingPrefs.setCompleted(context, userId, childId)

                    // 기존에 한 번 연결했던 워치 deviceId를 로컬에서 복원
                    // 현재 데모 계정(user 7 / child 6)은 이전 버전에서 deviceId를 저장하지 않았으므로
                    // 최초 1회에 한해 기존 DB 워치(id=1)를 fallback으로 사용한다.
                    val restoredDeviceId =
                        WatchDevicePrefs.getDeviceId(context, childId)
                            ?: if (userId == 7L && childId == 6L) 1L else null

                    savedDeviceId = restoredDeviceId

                    if (restoredDeviceId != null) {
                        WatchDevicePrefs.setDeviceId(
                            context = context,
                            childId = childId,
                            deviceId = restoredDeviceId
                        )
                    }

                    currentScreen = AppScreen.MAIN
                } else {
                    savedChildId = null
                    currentScreen = AppScreen.PERMISSION
                }
            },
            onNavigateToSignUp = { currentScreen = AppScreen.SIGN_UP }
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
                            savedChildId = childId
                            savedDeviceId = null
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

        AppScreen.MAIN, AppScreen.TIMELINE -> {
            val vm = homeViewModel
            if (vm == null) {
                Toast.makeText(context, "사용자 또는 아이 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                currentScreen = AppScreen.LOGIN
            } else {
                val parentName = onboardingViewModel.parentName.ifBlank { "보호자" }
                val initialTab = if (currentScreen == AppScreen.TIMELINE) BottomNavTab.TIMELINE else BottomNavTab.HOME

                MainHomeScreenContainer(
                    userId = loggedInUserId!!,
                    homeViewModel = vm,
                    parentName = parentName,
                    initialTab = initialTab,

                    onNavigateToConnectWatch = {
                        connectWatchScreenTitle = "워치 연결"
                        vm.fetchPairingCode()
                        currentScreen = AppScreen.CONNECT_WATCH
                    },

                    onNavigateToDeviceManage = {
                        // 이미 연결된 기기를 관리할 때는 새 페어링 코드를 발급하지 않는다.
                        connectWatchScreenTitle = "기기 관리"
                        currentScreen = AppScreen.CONNECT_WATCH
                    },

                    onNavigateToMealCapture = {
                        currentScreen = AppScreen.MEAL_CAPTURE
                    },

                    onNavigateToMealDetail = { record ->
                        selectedMealRecord = record
                        currentScreen = AppScreen.MEAL_DETAIL
                    },

                    onNavigateToLifeRecord = {
                        currentScreen = AppScreen.LIFE_RECORD
                    },

                    onLogout = {
                        // 로그아웃해도 워치 연결 정보는 유지한다.
                        // 다음 로그인 시 childId 기준으로 기존 deviceId를 복원한다.
                        loggedInUserId = null
                        authToken = null
                        savedChildId = null
                        savedDeviceId = null
                        RetrofitClient.accessToken = null
                        currentScreen = AppScreen.LOGIN
                    }
                )
            }
        }

        AppScreen.CONNECT_WATCH -> {
            val vm = homeViewModel
            if (vm == null) {
                Toast.makeText(context, "워치 연결 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                currentScreen = AppScreen.MAIN
            } else {
                val uiState by vm.uiState.collectAsState()

                LaunchedEffect(uiState.isDeviceConnected, uiState.deviceId) {
                    if (uiState.isDeviceConnected) {
                        val childId = effectiveChildId
                        val deviceId = uiState.deviceId

                        if (childId != null && deviceId != null) {
                            savedDeviceId = deviceId
                            WatchDevicePrefs.setDeviceId(
                                context = context,
                                childId = childId,
                                deviceId = deviceId
                            )
                        }

                        currentScreen = AppScreen.MAIN
                    }
                }

                // 💡 에러의 원인이었던 onSkipClick 파라미터를 완전히 제거했습니다!
                ConnectWatchScreen(
                    title = connectWatchScreenTitle,
                    code = uiState.pairingCode,
                    isLoading = uiState.isLoading,
                    onRefreshCode = {
                        // "워치 연결" 화면에서만 새 페어링 코드를 발급한다.
                        // "기기 관리"에서는 기존 연결을 유지한다.
                        if (connectWatchScreenTitle == "워치 연결") {
                            vm.fetchPairingCode()
                        }
                    },
                    onBackClick = { currentScreen = AppScreen.MAIN }
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
                    onBack = { currentScreen = AppScreen.MAIN }
                )
            } else {
                MealRecordInputScreen(
                    imageUri = capturedImageUri,
                    onBack = { showNextScreen = false },
                    onSubmitComplete = {
                        capturedImageUri = null
                        showNextScreen = false
                        currentScreen = AppScreen.TIMELINE
                    }
                )
            }
        }

        AppScreen.MEAL_DETAIL -> {
            val record = selectedMealRecord
            if (record == null) {
                currentScreen = AppScreen.TIMELINE
            } else {
                MealRecordDetailScreen(
                    record = record,
                    onBackClick = {
                        selectedMealRecord = null
                        currentScreen = AppScreen.TIMELINE
                    }
                )
            }
        }

        AppScreen.LIFE_RECORD -> {
            LifeRecordInputScreen(
                onBack = { currentScreen = AppScreen.MAIN },
                onSubmitComplete = { currentScreen = AppScreen.TIMELINE }
            )
        }
    }
}

@Composable
private fun MainHomeScreenContainer(
    userId: Long,
    homeViewModel: HomeViewModel,
    parentName: String,
    initialTab: BottomNavTab = BottomNavTab.HOME,
    onNavigateToConnectWatch: () -> Unit,
    onNavigateToDeviceManage: () -> Unit,
    onNavigateToMealCapture: () -> Unit,
    onNavigateToMealDetail: (MealRecord) -> Unit,
    onNavigateToLifeRecord: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val uiState by homeViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(initialTab) }

    LaunchedEffect(Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val hasFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocation || hasCoarseLocation) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        homeViewModel.fetchWeather(lat = location.latitude, lon = location.longitude)
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
                onConnectWatchClick = onNavigateToConnectWatch,
                onRefreshClick = {
                    homeViewModel.loadToday()
                    homeViewModel.fetchWeather(lat = 37.5665, lon = 126.9780)
                },
                onStartDetectionClick = { homeViewModel.onStartDetection() },
                onStopDetectionClick = { homeViewModel.onStopDetection() },
                onMealLogClick = onNavigateToMealCapture,
                onLifeLogClick = onNavigateToLifeRecord
            )
        }

        BottomNavTab.TIMELINE -> {
            TimelineScreen(
                homeViewModel = homeViewModel,
                selectedBottomTab = selectedTab,
                onBottomTabSelected = { selectedTab = it },
                onAddRecordClick = onNavigateToMealCapture,
                onRecordClick = onNavigateToMealDetail,
                onNavigateToLifeRecordInput = onNavigateToLifeRecord
            )
        }

        BottomNavTab.REPORT -> {
            DailyReportScreen(
                userId = userId,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        BottomNavTab.MY -> {
            MyPageScreen(
                guardianName = parentName,
                pairingCode = uiState.pairingCode,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onDeviceManageClick = onNavigateToDeviceManage,
                onLogoutClick = onLogout
            )
        }
    }
}