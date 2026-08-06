package com.example.scratchdetecter.presentation

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.scratchdetecter.detection.LOW_BATTERY_PERCENT
import com.example.scratchdetecter.detection.SERVER_SEND_COOLDOWN_MS
import com.example.scratchdetecter.detection.ScratchDetector
import com.example.scratchdetecter.detection.SensorCollector
import com.example.scratchdetecter.detection.SensorSample
import com.example.scratchdetecter.network.RetrofitClient
import com.example.scratchdetecter.network.dto.ScratchEventRequest
import com.example.scratchdetecter.network.dto.ScratchIngestRequest
import com.example.scratchdetecter.presentation.navigation.WatchScreenState
import com.example.scratchdetecter.presentation.screen.BatteryLowScreen
import com.example.scratchdetecter.presentation.screen.FinishedScreen
import com.example.scratchdetecter.presentation.screen.HomeScreen
import com.example.scratchdetecter.presentation.screen.MonitoringScreen
import com.example.scratchdetecter.presentation.screen.PairingCodeScreen
import com.example.scratchdetecter.presentation.screen.PairingIntroScreen
import com.example.scratchdetecter.presentation.screen.RestartScreen
import com.example.scratchdetecter.presentation.screen.WarningScreen
import com.example.scratchdetecter.presentation.theme.ScratchDetecterTheme
import com.example.scratchdetecter.storage.PairingPreference
import com.example.scratchdetecter.vibration.WatchVibrator
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {

    private var scratchDetector: ScratchDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        scratchDetector =
            try {
                ScratchDetector(applicationContext).also {
                    Log.d(
                        "TFLITE",
                        "모델 준비 완료: " +
                                "windowSize=${it.windowSize}, " +
                                "stepSize=${it.stepSize}, " +
                                "threshold=${it.threshold}"
                    )
                }
            } catch (exception: Exception) {
                Log.e(
                    "TFLITE",
                    "모델 준비 실패",
                    exception
                )

                null
            }

        setContent {
            ScratchDetecterTheme {
                WatchApp(
                    detector = scratchDetector
                )
            }
        }
    }

    override fun onDestroy() {
        scratchDetector?.close()
        scratchDetector = null

        super.onDestroy()
    }
}

@Composable
private fun WatchApp(
    detector: ScratchDetector?
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val pairingPreference =
        remember(context) {
            PairingPreference(context)
        }

    var screenState by remember {
        mutableStateOf(
            if (pairingPreference.isPaired()) {
                WatchScreenState.HOME
            } else {
                WatchScreenState.PAIRING_INTRO
            }
        )
    }

    var isUploading by remember {
        mutableStateOf(false)
    }

    val shouldMonitor =
        screenState == WatchScreenState.MONITORING ||
                screenState == WatchScreenState.WARNING

    DisposableEffect(
        context,
        detector,
        shouldMonitor
    ) {
        if (
            !shouldMonitor ||
            detector == null
        ) {
            onDispose { }
        } else {
            val sampleBuffer =
                mutableListOf<SensorSample>()

            var movementStoppedAt = 0L

            val movementStopThreshold = 0.35f
            val movementStopConfirmMs = 300L

            var scratchStartedAt: Long? = null
            var lastServerSendAt = 0L

// 긁음이 2초 이상 이어지면 경고 화면
            val warningThresholdMs = 2_000L


            val sensorCollector =
                SensorCollector(
                    context = context,
                    onSample = { sample ->
                        sampleBuffer.add(sample)

                        val now = System.currentTimeMillis()

                        val gyroMagnitude = sqrt(
                            sample.gyroX * sample.gyroX +
                                    sample.gyroY * sample.gyroY +
                                    sample.gyroZ * sample.gyroZ
                        )

                        if (scratchStartedAt != null) {
                            if (gyroMagnitude < movementStopThreshold) {
                                if (movementStoppedAt == 0L) {
                                    movementStoppedAt = now
                                }

                                if (
                                    now - movementStoppedAt >= movementStopConfirmMs
                                ) {
                                    WatchVibrator.stop(context)

                                    scratchStartedAt = null
                                    movementStoppedAt = 0L
                                    sampleBuffer.clear()

                                    screenState = WatchScreenState.FINISHED

                                    return@SensorCollector
                                }
                            } else {
                                movementStoppedAt = 0L
                            }
                        }

                        if (
                            sampleBuffer.size <
                            detector.windowSize
                        ) {
                            return@SensorCollector
                        }

                        try {
                            val window =
                                sampleBuffer.take(
                                    detector.windowSize
                                )

                            val probability =
                                detector.predict(window)

                            val isScratch =
                                detector.isScratch(probability)

                            Log.d(
                                "PREDICTION",
                                "probability=$probability, " +
                                        "threshold=${detector.threshold}, " +
                                        "isScratch=$isScratch"
                            )

                            if (isScratch) {
                                /*
                                 * 처음 긁음이 감지된 시각을 저장한다.
                                 */
                                if (scratchStartedAt == null) {
                                    scratchStartedAt = now
                                }

                                /*
                                 * 긁음 판정이 나오는 동안 계속 진동한다.
                                 */
                                WatchVibrator.warning(context)

                                val scratchDuration =
                                    now - (scratchStartedAt ?: now)

                                /*
                                 * 긁음이 2초 이상 이어졌을 때만 경고 화면으로 변경한다.
                                 */
                                if (
                                    scratchDuration >= warningThresholdMs &&
                                    screenState != WatchScreenState.WARNING
                                ) {
                                    screenState = WatchScreenState.WARNING
                                }

                                val serverCooldownPassed =
                                    now - lastServerSendAt >= SERVER_SEND_COOLDOWN_MS

                                if (
                                    serverCooldownPassed &&
                                    !isUploading
                                ) {
                                    lastServerSendAt = now
                                    isUploading = true

                                    coroutineScope.launch {
                                        try {
                                            sendDetectedScratchEvent(
                                                pairingPreference = pairingPreference,
                                                probability = probability,
                                                detectedAtMillis = now
                                            )

                                            Log.d(
                                                "SCRATCH_API",
                                                "긁음 이벤트 전송 성공"
                                            )
                                        } catch (exception: Exception) {
                                            lastServerSendAt = 0L

                                            Log.e(
                                                "SCRATCH_API",
                                                "긁음 이벤트 전송 실패",
                                                exception
                                            )
                                        } finally {
                                            isUploading = false
                                        }
                                    }
                                }
                            } else {
                                if (scratchStartedAt != null) {
                                    WatchVibrator.stop(context)
                                    scratchStartedAt = null
                                    movementStoppedAt = 0L
                                    sampleBuffer.clear()

                                    screenState =
                                        WatchScreenState.FINISHED
                                } else {
                                    WatchVibrator.stop(context)
                                }
                            }

                        } catch (exception: Exception) {
                            Log.e(
                                "PREDICTION",
                                "긁음 예측 실패",
                                exception
                            )
                        } finally {
                            val removeCount =
                                detector.stepSize.coerceAtMost(
                                    sampleBuffer.size
                                )

                            repeat(removeCount) {
                                sampleBuffer.removeAt(0)
                            }
                        }
                    }
                )

            try {
                sensorCollector.start()

                Log.d(
                    "SENSOR",
                    "센서 수집 시작"
                )
            } catch (exception: Exception) {
                Log.e(
                    "SENSOR",
                    "센서 수집 시작 실패",
                    exception
                )
            }

            onDispose {
                sensorCollector.stop()
                sampleBuffer.clear()

                Log.d(
                    "SENSOR",
                    "센서 수집 종료"
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181818)),
        contentAlignment = Alignment.Center
    ) {
        when (screenState) {
            WatchScreenState.PAIRING_INTRO -> {
                PairingIntroScreen(
                    onPairClick = {
                        screenState =
                            WatchScreenState.PAIRING_CODE
                    }
                )
            }

            WatchScreenState.PAIRING_CODE -> {
                PairingCodeScreen(
                    onPairingSuccess = { serverDeviceId, deviceName ->
                        pairingPreference.savePairedDevice(
                            serverDeviceId = serverDeviceId,
                            deviceName = deviceName
                        )

                        screenState =
                            WatchScreenState.HOME
                    },
                    onClose = {
                        screenState =
                            WatchScreenState.PAIRING_INTRO
                    }
                )
            }

            WatchScreenState.HOME -> {
                HomeScreen(
                    onStart = {
                        val batteryPercentage =
                            getBatteryPercentage(context)

                        screenState =
                            if (
                                batteryPercentage in
                                0..LOW_BATTERY_PERCENT
                            ) {
                                WatchScreenState.BATTERY_LOW
                            } else {
                                WatchScreenState.MONITORING
                            }
                    }
                )
            }

            WatchScreenState.MONITORING -> {
                MonitoringScreen(
                    onStop = {
                        screenState =
                            WatchScreenState.FINISHED
                    }
                )
            }

            WatchScreenState.WARNING -> {
                WarningScreen(
                    onTimeout = {
                        // 긁기를 멈출 때까지 WARNING 화면 유지
                    }
                )
            }

            WatchScreenState.FINISHED -> {
                FinishedScreen(
                    onTimeout = {
                        screenState =
                            WatchScreenState.MONITORING
                    }
                )
            }

            WatchScreenState.BATTERY_LOW -> {
                BatteryLowScreen(
                    onPause = {
                        screenState =
                            WatchScreenState.HOME
                    }
                )
            }
        }
    }
}

/**
 * 워치의 현재 배터리 잔량을 가져온다.
 *
 * 값을 구할 수 없으면 -1을 반환한다.
 */
private fun getBatteryPercentage(
    context: Context
): Int {
    val batteryIntent =
        context.registerReceiver(
            null,
            IntentFilter(
                Intent.ACTION_BATTERY_CHANGED
            )
        ) ?: return -1

    val level =
        batteryIntent.getIntExtra(
            BatteryManager.EXTRA_LEVEL,
            -1
        )

    val scale =
        batteryIntent.getIntExtra(
            BatteryManager.EXTRA_SCALE,
            -1
        )

    if (
        level < 0 ||
        scale <= 0
    ) {
        return -1
    }

    return (
            level * 100f /
                    scale.toFloat()
            ).toInt()
}

private suspend fun sendDetectedScratchEvent(
    pairingPreference: PairingPreference,
    probability: Float,
    detectedAtMillis: Long
) {
    val endTime =
        Instant.ofEpochMilli(
            detectedAtMillis
        )

    /*
     * 모델 입력 윈도우가 2초이므로
     * 감지 시점 2초 전을 이벤트 시작으로 기록한다.
     */
    val startTime =
        endTime.minusSeconds(2)

    val intensity =
        when {
            probability >= 0.9f -> 5
            probability >= 0.8f -> 4
            probability >= 0.7f -> 3
            probability >= 0.6f -> 2
            else -> 1
        }

    val deviceId =
        pairingPreference
            .serverDeviceIdOrDefault(
                defaultValue = 1L
            )

    val request =
        ScratchIngestRequest(
            deviceId = deviceId,
            modelVersion =
                "scratch-binary-v1",
            calibrationVersion = 1,
            schemaVersion = 1,
            events = listOf(
                ScratchEventRequest(
                    clientEventId =
                        UUID.randomUUID()
                            .toString(),
                    startTs =
                        startTime.toString(),
                    endTs =
                        endTime.toString(),
                    durationSec = 2.0,
                    intensity = intensity,
                    confidence =
                        probability.toDouble(),
                    windowCount = 1,
                    wearPosition = "LEFT"
                )
            ),
            wearSecondsInBatch = 2L
        )

    val response =
        RetrofitClient.scratchApi
            .sendScratchEvents(
                userId = 1L,
                idempotencyKey =
                    UUID.randomUUID()
                        .toString(),
                backfill = false,
                request = request
            )

    if (!response.isSuccessful) {
        val errorBody =
            response.errorBody()
                ?.string()

        throw IllegalStateException(
            "서버 응답 ${response.code()}: $errorBody"
        )
    }
}