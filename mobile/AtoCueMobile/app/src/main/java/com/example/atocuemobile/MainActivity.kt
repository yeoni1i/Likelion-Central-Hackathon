package com.example.atocuemobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.atocuemobile.ui.component.BottomNavigationBar
import com.example.atocuemobile.ui.component.BottomNavTab
import com.example.atocuemobile.ui.screen.LoginScreen
import com.example.atocuemobile.ui.screen.PairingTestScreen
import com.example.atocuemobile.ui.screen.ScratchTestScreen
import com.example.atocuemobile.ui.screen.SignUpScreen
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
                    AppRoot()
                }
            }
        }
    }
}

/** 앱 전체 화면 상태 */
private enum class AppScreen {
    LOGIN,
    SIGN_UP,
    MAIN
}

@Composable
private fun AppRoot() {
    var currentScreen by remember { mutableStateOf(AppScreen.LOGIN) }
    // 로그인 성공 시 받은 정보. 나중에 다른 화면에서 API 호출할 때 필요하면 씀.
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
                // 회원가입 성공하면 로그인 화면으로 돌려보냄
                currentScreen = AppScreen.LOGIN
            },
            onNavigateBack = {
                currentScreen = AppScreen.LOGIN
            }
        )

        AppScreen.MAIN -> TestApp()
    }
}

private enum class TestScreen {
    PAIRING,
    SCRATCH
}

@Composable
private fun TestApp() {
    var selectedScreen by remember {
        mutableStateOf<TestScreen?>(null)
    }
    var selectedTab by remember { mutableStateOf(BottomNavTab.HOME) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                onClick = {
                    selectedScreen = TestScreen.PAIRING
                }
            ) {
                Text("워치 등록")
            }

            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                onClick = {
                    selectedScreen = TestScreen.SCRATCH
                }
            ) {
                Text("긁음 조회")
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (selectedScreen) {
                TestScreen.PAIRING -> PairingTestScreen()
                TestScreen.SCRATCH -> ScratchTestScreen()
                null -> {
                    Text(
                        text = "테스트할 기능을 선택해주세요.",
                        modifier = Modifier.padding(20.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        BottomNavigationBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
    }
}