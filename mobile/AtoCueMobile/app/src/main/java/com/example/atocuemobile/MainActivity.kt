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
import com.example.atocuemobile.ui.screen.PairingTestScreen
import com.example.atocuemobile.ui.screen.ScratchTestScreen
import com.example.atocuemobile.ui.theme.AtoCueMobileTheme
import com.example.atocuemobile.ui.screen.timeline.TimelineScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AtoCueMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    TestApp()
                    TimelineScreen(onAddRecordClick = {})
                }
            }
        }
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
    }
}