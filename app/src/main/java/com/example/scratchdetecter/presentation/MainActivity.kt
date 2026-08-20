package com.example.scratchdetecter.presentation

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import com.example.scratchdetecter.detection.ScratchDetector
import com.example.scratchdetecter.detection.ScratchEpisodeTracker
import com.example.scratchdetecter.detection.ScratchIntensityCalculator
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
import com.example.scratchdetecter.presentation.screen.WarningScreen
import com.example.scratchdetecter.presentation.theme.ScratchDetecterTheme
import com.example.scratchdetecter.storage.PairingPreference
import com.example.scratchdetecter.vibration.WatchVibrator
import java.time.Instant
import java.util.UUID
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private var scratchDetector: ScratchDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        scratchDetector =
            try {
                ScratchDetector(applicationContext)
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

    val coroutineScope =
        rememberCoroutineScope()

    val pairingPreference =
        remember(context) {
            PairingPreference(context)
        }


    /*
     * 여러 개의 AI scratch window를
     * 하나의 실제 긁음 episode로 묶어주는 객체
     */
    val episodeTracker =
        remember {
            ScratchEpisodeTracker()
        }


    var savedDeviceId by
    remember {
        mutableStateOf(
            pairingPreference.serverDeviceId()
        )
    }


    var screenState by
    remember {
        mutableStateOf(
            if (savedDeviceId != null) {
                WatchScreenState.HOME
            } else {
                WatchScreenState.PAIRING_INTRO
            }
        )
    }


    var hasStartedMonitoring by
    remember {
        mutableStateOf(false)
    }


    /*
     * WARNING 화면용 시간.
     *
     * 이건 DB의 긁음 시작시간이 아니라
     * "AI가 처음 scratch라고 판정한 시점"을 기록한다.
     *
     * 2초 이상 scratch가 이어졌을 때
     * WARNING 화면을 띄우기 위해 사용한다.
     */
    var warningStartedAt by
    remember {
        mutableStateOf<Long?>(null)
    }

    var serverDetectionActive by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        Log.d(
            "DETECTION_POLL",
            "polling coroutine 시작"
        )

        while (true) {

            try {

                val deviceId =
                    pairingPreference.serverDeviceId()

                if (deviceId == null) {

                    Log.d(
                        "DETECTION_POLL",
                        "deviceId 없음 - 대기"
                    )

                    delay(2000)
                    continue
                }

                val response =
                    RetrofitClient
                        .deviceDetectionApi
                        .getDetectionStatus(deviceId)

                if (response.isSuccessful) {

                    val status =
                        response.body()?.status

                    Log.d(
                        "DETECTION_POLL",
                        "서버 감지 상태 = $status"
                    )

                    when (status) {

                        "START" -> {

                            serverDetectionActive = true

                            if (
                                screenState != WatchScreenState.MONITORING &&
                                screenState != WatchScreenState.WARNING &&
                                screenState != WatchScreenState.FINISHED
                            ) {

                                Log.d(
                                    "DETECTION_POLL",
                                    "START 수신 → 감지 시작"
                                )

                                hasStartedMonitoring = false

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
                        }

                        "STOP" -> {

                            serverDetectionActive = false

                            if (
                                screenState == WatchScreenState.MONITORING ||
                                screenState == WatchScreenState.WARNING ||
                                screenState == WatchScreenState.FINISHED
                            ) {

                                Log.d(
                                    "DETECTION_POLL",
                                    "STOP 수신 → 감지 종료"
                                )

                                WatchVibrator.stop(context)
                                warningStartedAt = null

                                screenState =
                                    WatchScreenState.HOME
                            }
                        }
                    }

                } else {

                    Log.e(
                        "DETECTION_POLL",
                        "상태 조회 실패 HTTP=${response.code()}"
                    )
                }

            } catch (e: CancellationException) {

                Log.d(
                    "DETECTION_POLL",
                    "polling coroutine 종료"
                )

                throw e

            } catch (e: Exception) {

                Log.e(
                    "DETECTION_POLL",
                    "상태 polling 실패",
                    e
                )
            }

            delay(2000)
        }
    }

    /*
     * 워치의 뒤로가기 버튼을 눌렀을 때
     * 진행 중인 episode가 있다면 마지막 데이터를 저장하고
     * HOME 화면으로 이동한다.
     */
    BackHandler(
        enabled =
            screenState == WatchScreenState.MONITORING ||
                    screenState == WatchScreenState.WARNING
    ) {

        WatchVibrator.stop(context)

        val unfinishedEpisode =
            episodeTracker.forceFinish()

        if (unfinishedEpisode != null) {

            coroutineScope.launch {

                try {
                    sendScratchEpisode(
                        pairingPreference =
                            pairingPreference,
                        episode =
                            unfinishedEpisode
                    )

                    Log.d(
                        "SCRATCH_API",
                        "감지 종료 전 마지막 episode 전송 성공"
                    )

                } catch (exception: Exception) {

                    Log.e(
                        "SCRATCH_API",
                        "감지 종료 전 마지막 episode 전송 실패",
                        exception
                    )
                }
            }
        }

        warningStartedAt = null

        hasStartedMonitoring = true

        screenState =
            WatchScreenState.HOME
    }


    /*
     * MONITORING 또는 WARNING 화면일 때만
     * 실제 센서 측정을 진행한다.
     */
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

            /*
             * AI 모델에 전달할 센서 데이터를
             * 임시로 모아두는 버퍼
             */
            val sampleBuffer =
                mutableListOf<SensorSample>()


            /*
             * AI scratch 판정이 2초 이상 이어지면
             * WARNING 화면으로 변경한다.
             */
            val warningThresholdMs =
                2_000L


            val sensorCollector =
                SensorCollector(
                    context = context,

                    onSample = { sample ->

                        /*
                         * 20ms마다 들어오는 센서값을
                         * sampleBuffer에 계속 추가한다.
                         */
                        sampleBuffer.add(sample)

                        val now =
                            System.currentTimeMillis()


                        /*
                         * AI window 크기만큼 센서값이
                         * 모이지 않았다면 아직 추론하지 않는다.
                         */
                        if (
                            sampleBuffer.size <
                            detector.windowSize
                        ) {
                            return@SensorCollector
                        }


                        try {

                            //window 추출
                            val window =
                                sampleBuffer.take(
                                    detector.windowSize
                                )


                            /*
                             * scratch 확률을 계산
                             */
                            val probability =
                                detector.predict(window)


                            //scratch / non-scratch 결정
                            val isScratch =
                                detector.isScratch(
                                    probability
                                )


                            Log.d(
                                "PREDICTION",
                                "probability=$probability, " +
                                        "threshold=${detector.threshold}, " +
                                        "isScratch=$isScratch"
                            )


                            if (isScratch) {

                                val accelRms =
                                    ScratchIntensityCalculator.calculateRms(
                                        window
                                    )

                                Log.d(
                                    "SCRATCH_RMS",
                                    "rms=$accelRms, probability=$probability"
                                )

                                val intensity =
                                    ScratchIntensityCalculator.calculate(
                                        window
                                    )

                                /*
                                 * 이번 scratch window 정보를
                                 * episodeTracker에 추가한다.
                                 *
                                 * 이 시점에는 서버로 보내지 않는다.
                                 */
                                episodeTracker.onScratch(
                                    detectedAtMillis =
                                        now,
                                    confidence =
                                        probability.toDouble(),
                                    intensity =
                                        intensity
                                )


                                /*
                                 * 처음 scratch 판정이 나온 순간을
                                 * WARNING 판단용으로 기억한다.
                                 */
                                if (
                                    warningStartedAt == null
                                ) {
                                    warningStartedAt =
                                        now
                                }


                                /*
                                 * 긁음 판정이 이어지는 동안
                                 * 진동 경고
                                 */
                                WatchVibrator.warning(
                                    context
                                )


                                /*
                                 * AI가 scratch라고 판정하기 시작한 후
                                 * 얼마나 지났는지 계산
                                 */
                                val warningDuration =
                                    now -
                                            (
                                                    warningStartedAt
                                                        ?: now
                                                    )


                                /*
                                 * 2초 이상 긁음이 이어졌다면
                                 * WARNING 화면으로 전환
                                 */
                                if (
                                    warningDuration >=
                                    warningThresholdMs &&
                                    screenState !=
                                    WatchScreenState.WARNING
                                ) {

                                    screenState =
                                        WatchScreenState.WARNING
                                }


                            } else {

                                /*
                                 * non-scratch가 한 번 나왔다고
                                 * 바로 episode를 끝내지 않는다.
                                 *
                                 * ScratchEpisodeTracker 내부의
                                 * 2초 grace period를 확인한다.
                                 */
                                val finishedEpisode =
                                    episodeTracker
                                        .checkEpisodeEnd(
                                            now
                                        )


                                /*
                                 * null이 아니면
                                 * 마지막 scratch 이후 2초 이상
                                 * scratch가 없었다는 뜻이다.
                                 */
                                if (
                                    finishedEpisode != null
                                ) {

                                    WatchVibrator.stop(
                                        context
                                    )

                                    warningStartedAt =
                                        null


                                    /*
                                     * 하나의 실제 긁음 episode가
                                     * 완성됐으므로 이때 딱 한 번
                                     * Spring Boot 서버로 전송한다.
                                     */
                                    coroutineScope.launch {

                                        try {

                                            sendScratchEpisode(
                                                pairingPreference =
                                                    pairingPreference,
                                                episode =
                                                    finishedEpisode
                                            )


                                            Log.d(
                                                "SCRATCH_API",
                                                "긁음 episode 전송 성공: " +
                                                        "duration=${finishedEpisode.durationSec}, " +
                                                        "windows=${finishedEpisode.windowCount}, " +
                                                        "confidence=${finishedEpisode.averageConfidence}, " +
                                                        "intensity=${finishedEpisode.maxIntensity}"
                                            )

                                        } catch (
                                            exception: Exception
                                        ) {

                                            Log.e(
                                                "SCRATCH_API",
                                                "긁음 episode 전송 실패",
                                                exception
                                            )
                                        }
                                    }


                                    /*
                                     * 긁음이 끝났다는 화면 표시
                                     */
                                    screenState =
                                        WatchScreenState.FINISHED
                                }
                            }


                        } catch (
                            exception: Exception
                        ) {

                            Log.e(
                                "PREDICTION",
                                "긁음 예측 실패",
                                exception
                            )

                        } finally {

                            /*
                             * 현재 모델의 step size만큼
                             * 앞쪽 센서 데이터를 제거한다.
                             *
                             * 예:
                             * window = 100개
                             * step = 50개
                             *
                             * → 다음 window와 50개가 겹친다.
                             */
                            val removeCount =
                                detector.stepSize
                                    .coerceAtMost(
                                        sampleBuffer.size
                                    )


                            repeat(removeCount) {
                                sampleBuffer.removeAt(0)
                            }
                        }
                    }
                )


            /*
             * 센서 수집 시작
             */
            try {

                sensorCollector.start()

                Log.d(
                    "SENSOR",
                    "센서 수집 시작"
                )

            } catch (
                exception: Exception
            ) {

                Log.e(
                    "SENSOR",
                    "센서 수집 시작 실패",
                    exception
                )
            }


            /*
             * MONITORING / WARNING 화면을 벗어나면
             * 센서 수집 종료
             */
            onDispose {

                sensorCollector.stop()

                sampleBuffer.clear()

                WatchVibrator.stop(
                    context
                )

                Log.d(
                    "SENSOR",
                    "센서 수집 종료"
                )
            }
        }
    }


    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Color(0xFF181818)
                ),
        contentAlignment =
            Alignment.Center
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

                    onPairingSuccess = {
                            serverDeviceId,
                            deviceName ->

                        pairingPreference.savePairedDevice(
                            serverDeviceId = serverDeviceId,
                            deviceName = deviceName
                        )

                        savedDeviceId = serverDeviceId

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

                    buttonText =
                        if (
                            hasStartedMonitoring
                        ) {
                            "재시작"
                        } else {
                            "감지 시작"
                        },

                    onStart = {

                        hasStartedMonitoring =
                            true

                        val batteryPercentage =
                            getBatteryPercentage(
                                context
                            )

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

                        /*
                         * 사용자가 직접 감지 종료 버튼을
                         * 눌렀을 경우에도 진행 중 episode를
                         * 마지막으로 저장한다.
                         */
                        val unfinishedEpisode =
                            episodeTracker.forceFinish()


                        if (
                            unfinishedEpisode != null
                        ) {

                            coroutineScope.launch {

                                try {

                                    sendScratchEpisode(
                                        pairingPreference =
                                            pairingPreference,
                                        episode =
                                            unfinishedEpisode
                                    )

                                    Log.d(
                                        "SCRATCH_API",
                                        "수동 종료 episode 전송 성공"
                                    )

                                } catch (
                                    exception: Exception
                                ) {

                                    Log.e(
                                        "SCRATCH_API",
                                        "수동 종료 episode 전송 실패",
                                        exception
                                    )
                                }
                            }
                        }


                        warningStartedAt =
                            null

                        WatchVibrator.stop(
                            context
                        )


                        screenState =
                            WatchScreenState.FINISHED
                    }
                )
            }


            WatchScreenState.WARNING -> {

                WarningScreen(

                    onTimeout = {
                        /*
                         * 긁기를 멈출 때까지
                         * WARNING 화면 유지
                         */
                    }
                )
            }


            WatchScreenState.FINISHED -> {

                FinishedScreen(
                    onTimeout = {

                        screenState =
                            if (serverDetectionActive) {
                                WatchScreenState.MONITORING
                            } else {
                                WatchScreenState.HOME
                            }
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
        )
            ?: return -1


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


/*
 * 하나의 완성된 ScratchEpisode를
 * Spring Boot 서버에 전송한다.
 *
 * 예전처럼 AI window 하나마다 보내는 게 아니라
 * 실제 긁음 episode가 끝났을 때 한 번만 호출된다.
 */
private suspend fun sendScratchEpisode(
    pairingPreference: PairingPreference,
    episode: ScratchEpisodeTracker.ScratchEpisode
) {

    val deviceId =
        pairingPreference
            .serverDeviceId()
            ?: throw IllegalStateException(
                "연동된 워치의 서버 deviceId가 없습니다."
            )


    val startTime =
        Instant.ofEpochMilli(
            episode.startTimeMillis
        )


    val endTime =
        Instant.ofEpochMilli(
            episode.endTimeMillis
        )


    val request =
        ScratchIngestRequest(

            deviceId =
                deviceId,

            modelVersion =
                "scratch-binary-v1",

            calibrationVersion =
                1,

            schemaVersion =
                1,

            events =
                listOf(

                    ScratchEventRequest(

                        clientEventId =
                            UUID
                                .randomUUID()
                                .toString(),

                        startTs =
                            startTime.toString(),

                        endTs =
                            endTime.toString(),

                        /*
                         * 한 번의 실제 긁음 지속시간
                         */
                        durationSec =
                            episode.durationSec,

                        /*
                         * episode 동안 가장 높은 강도
                         */
                        intensity =
                            episode.maxIntensity,

                        /*
                         * scratch window들의 평균 confidence
                         */
                        confidence =
                            episode.averageConfidence,

                        /*
                         * episode 안에서 scratch로
                         * 판정된 AI window 개수
                         */
                        windowCount =
                            episode.windowCount,

                        wearPosition =
                            "LEFT"
                    )
                ),

            wearSecondsInBatch =
                episode
                    .durationSec
                    .toLong()
                    .coerceAtLeast(1L)
        )


    val response =
        RetrofitClient
            .scratchApi
            .sendScratchEvents(

                idempotencyKey =
                    UUID
                        .randomUUID()
                        .toString(),

                backfill =
                    false,

                request =
                    request
            )


    if (
        !response.isSuccessful
    ) {

        val errorBody =
            response
                .errorBody()
                ?.string()


        throw IllegalStateException(
            "서버 응답 ${response.code()}: $errorBody"
        )
    }
}