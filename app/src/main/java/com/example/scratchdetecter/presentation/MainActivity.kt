package com.example.scratchdetecter.presentation

import android.content.Intent
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.example.scratchdetecter.network.RetrofitClient
import com.example.scratchdetecter.network.ScratchEventRequest
import com.example.scratchdetecter.network.ScratchIngestRequest
import com.example.scratchdetecter.presentation.theme.ScratchDetecterTheme
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.Instant
import java.util.UUID
import kotlin.math.sqrt
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.scratchdetecter.R

private const val MODEL_FILE_NAME = "scratch_binary_model.tflite"
private const val CONFIG_FILE_NAME = "scratch_binary_config.json"

private const val SAMPLE_INTERVAL_MS = 20L
private const val DEFAULT_WINDOW_SIZE = 100
private const val DEFAULT_STEP_SIZE = 50
private const val VIBRATION_COOLDOWN_MS = 2_000L
private const val SERVER_SEND_COOLDOWN_MS = 5_000L

data class SensorSample(
    val accX: Float,
    val accY: Float,
    val accZ: Float,
    val gyroX: Float,
    val gyroY: Float,
    val gyroZ: Float
)

data class ModelConfig(
    val featureColumns: List<String>,
    val means: FloatArray,
    val scales: FloatArray,
    val threshold: Float,
    val windowSize: Int,
    val stepSize: Int
)

private enum class WatchScreenState {
    HOME,
    MONITORING,
    WARNING,
    FINISHED
}

class MainActivity : ComponentActivity() {

    private var interpreter: Interpreter? = null
    private var modelConfig: ModelConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val modelBuffer = loadModelFile(
                context = this,
                modelName = MODEL_FILE_NAME
            )

            interpreter = Interpreter(modelBuffer)

            modelConfig = loadModelConfig(
                context = this,
                configName = CONFIG_FILE_NAME
            )

            val inputShape = interpreter
                ?.getInputTensor(0)
                ?.shape()

            val outputShape = interpreter
                ?.getOutputTensor(0)
                ?.shape()

            val featureCount = modelConfig?.featureColumns?.size ?: 0
            val modelInputCount = inputShape?.lastOrNull() ?: 0

            if (featureCount != modelInputCount) {
                throw IllegalStateException(
                    "모델 입력 개수($modelInputCount)와 설정 특징 개수($featureCount)가 다릅니다."
                )
            }

            Log.d("TFLITE", "Model loaded successfully")
            Log.d("TFLITE", "Input shape: ${inputShape?.contentToString()}")
            Log.d("TFLITE", "Output shape: ${outputShape?.contentToString()}")
            Log.d("TFLITE", "Feature count: $featureCount")
            Log.d("TFLITE", "Threshold: ${modelConfig?.threshold}")
        } catch (exception: Exception) {
            Log.e(
                "TFLITE",
                "Failed to prepare model",
                exception
            )
        }

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        setContent {
            ScratchDetecterTheme {
                ScratchDetectionScreen(
                    interpreter = interpreter,
                    modelConfig = modelConfig
                )
            }
        }
    }

    override fun onDestroy() {
        interpreter?.close()
        interpreter = null
        super.onDestroy()
    }
}

@Composable
fun ScratchDetectionScreen(
    interpreter: Interpreter?,
    modelConfig: ModelConfig?
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var screenState by remember {
        mutableStateOf(WatchScreenState.HOME)
    }

    var serverTestResult by remember {
        mutableStateOf("자동 기록 준비 완료")
    }

    var scratchProbability by remember {
        mutableFloatStateOf(0f)
    }

    var detectionText by remember {
        mutableStateOf("감지 대기")
    }

    val isMonitoring =
        screenState == WatchScreenState.MONITORING ||
                screenState == WatchScreenState.WARNING

    DisposableEffect(
        context,
        interpreter,
        modelConfig,
        isMonitoring
    ) {
        if (
            !isMonitoring ||
            interpreter == null ||
            modelConfig == null
        ) {
            onDispose { }
        } else {
            val sensorManager =
                context.getSystemService(
                    Context.SENSOR_SERVICE
                ) as SensorManager

            val accelerometer =
                sensorManager.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER
                )

            val gyroscope =
                sensorManager.getDefaultSensor(
                    Sensor.TYPE_GYROSCOPE
                )

            val sampleBuffer = mutableListOf<SensorSample>()
            val windowSize = modelConfig.windowSize
            val stepSize = modelConfig.stepSize

            var latestAccX = 0f
            var latestAccY = 0f
            var latestAccZ = 0f

            var latestGyroX = 0f
            var latestGyroY = 0f
            var latestGyroZ = 0f

            var lastSampleTime = 0L
            var lastVibrationTime = 0L
            var lastServerSendTime = 0L

            val sensorListener =
                object : SensorEventListener {

                    override fun onSensorChanged(
                        event: SensorEvent?
                    ) {
                        event ?: return

                        when (event.sensor.type) {
                            Sensor.TYPE_ACCELEROMETER -> {
                                latestAccX = event.values[0]
                                latestAccY = event.values[1]
                                latestAccZ = event.values[2]
                            }

                            Sensor.TYPE_GYROSCOPE -> {
                                latestGyroX = event.values[0]
                                latestGyroY = event.values[1]
                                latestGyroZ = event.values[2]
                            }
                        }

                        val currentTime =
                            System.currentTimeMillis()

                        if (
                            currentTime - lastSampleTime <
                            SAMPLE_INTERVAL_MS
                        ) {
                            return
                        }

                        lastSampleTime = currentTime

                        sampleBuffer.add(
                            SensorSample(
                                accX = latestAccX,
                                accY = latestAccY,
                                accZ = latestAccZ,
                                gyroX = latestGyroX,
                                gyroY = latestGyroY,
                                gyroZ = latestGyroZ
                            )
                        )

                        if (sampleBuffer.size < windowSize) {
                            detectionText =
                                "데이터 수집 ${sampleBuffer.size}/$windowSize"
                            return
                        }

                        try {
                            val window =
                                sampleBuffer.take(windowSize)

                            val featureMap =
                                extractFeatures(window)

                            val orderedFeatures =
                                createOrderedFeatureArray(
                                    featureMap = featureMap,
                                    featureColumns =
                                        modelConfig.featureColumns
                                )

                            val normalizedFeatures =
                                normalizeFeatures(
                                    features = orderedFeatures,
                                    means = modelConfig.means,
                                    scales = modelConfig.scales
                                )

                            val probability =
                                runInference(
                                    interpreter = interpreter,
                                    input = normalizedFeatures
                                )

                            scratchProbability = probability

                            val isScratch =
                                probability >= modelConfig.threshold

                            detectionText =
                                if (isScratch) {
                                    "긁기 감지"
                                } else {
                                    "일상 동작"
                                }

                            screenState =
                                if (isScratch) {
                                    WatchScreenState.WARNING
                                } else {
                                    WatchScreenState.MONITORING
                                }

                            Log.d(
                                "PREDICTION",
                                "probability=$probability, " +
                                        "threshold=${modelConfig.threshold}, " +
                                        "result=$detectionText"
                            )

                            if (isScratch) {
                                val now =
                                    System.currentTimeMillis()

                                if (
                                    now - lastVibrationTime >=
                                    VIBRATION_COOLDOWN_MS
                                ) {
                                    vibrateWatch(context)
                                    lastVibrationTime = now
                                }

                                if (
                                    now - lastServerSendTime >=
                                    SERVER_SEND_COOLDOWN_MS
                                ) {
                                    lastServerSendTime = now
                                    serverTestResult =
                                        "긁음 자동 저장 중"

                                    coroutineScope.launch {
                                        try {
                                            serverTestResult =
                                                sendDetectedScratchEvent(
                                                    probability = probability,
                                                    detectedAtMillis = now
                                                )

                                            Log.d(
                                                "SCRATCH_API",
                                                "자동 전송 성공: " +
                                                        "probability=$probability"
                                            )
                                        } catch (exception: Exception) {
                                            serverTestResult =
                                                "자동 저장 실패"
                                            lastServerSendTime = 0L

                                            Log.e(
                                                "SCRATCH_API",
                                                "긁음 자동 전송 실패",
                                                exception
                                            )
                                        }
                                    }
                                }
                            }
                        } catch (exception: Exception) {
                            detectionText = "예측 오류"

                            Log.e(
                                "PREDICTION",
                                "Prediction failed",
                                exception
                            )
                        }

                        val removeCount =
                            stepSize.coerceAtMost(
                                sampleBuffer.size
                            )

                        repeat(removeCount) {
                            sampleBuffer.removeAt(0)
                        }
                    }

                    override fun onAccuracyChanged(
                        sensor: Sensor?,
                        accuracy: Int
                    ) {
                        // 현재 단계에서는 사용하지 않는다.
                    }
                }

            accelerometer?.let {
                sensorManager.registerListener(
                    sensorListener,
                    it,
                    SensorManager.SENSOR_DELAY_GAME
                )
            }

            gyroscope?.let {
                sensorManager.registerListener(
                    sensorListener,
                    it,
                    SensorManager.SENSOR_DELAY_GAME
                )
            }

            onDispose {
                sensorManager.unregisterListener(
                    sensorListener
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF202124)),
        contentAlignment = Alignment.Center
    ) {
        when (screenState) {
            WatchScreenState.HOME -> {
                HomeScreen(
                    onStart = {
                        scratchProbability = 0f
                        detectionText = "감지 준비 중"
                        serverTestResult = "자동 기록 준비 완료"
                        screenState = WatchScreenState.MONITORING
                    },
                    onPair = {
                        context.startActivity(
                            Intent(
                                context,
                                PairActivity::class.java
                            )
                        )
                    }
                )
            }

            WatchScreenState.MONITORING -> {
                MonitoringScreen(
                    detectionText = detectionText,
                    serverStatus = serverTestResult,
                    onStop = {
                        screenState = WatchScreenState.HOME
                    }
                )
            }

            WatchScreenState.WARNING -> {
                WarningScreen(
                    onStop = {
                        screenState = WatchScreenState.FINISHED
                    }
                )
            }

            WatchScreenState.FINISHED -> {
                FinishedScreen(
                    onRestart = {
                        scratchProbability = 0f
                        detectionText = "감지 준비 중"
                        serverTestResult = "자동 기록 준비 완료"
                        screenState = WatchScreenState.MONITORING
                    },
                    onHome = {
                        screenState = WatchScreenState.HOME
                    }
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(
    onStart: () -> Unit,
    onPair: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 20.dp,
                vertical = 8.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFF4A4648),
                    shape = CircleShape
                )
                .padding(
                    horizontal = 13.dp,
                    vertical = 3.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "안녕!",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Image(
            painter = painterResource(
                id = R.drawable.atocue_character
            ),
            contentDescription = "AtoCue 캐릭터",
            modifier = Modifier.size(70.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Button(
            onClick = onStart,
            modifier = Modifier
                .width(104.dp)
                .height(34.dp),
            colors = androidx.wear.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 10.dp,
                vertical = 0.dp
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "감지 시작",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Button(
            onClick = onPair,
            modifier = Modifier
                .width(104.dp)
                .height(34.dp),
            colors = androidx.wear.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 10.dp,
                vertical = 0.dp
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "워치 등록",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun MonitoringScreen(
    detectionText: String,
    serverStatus: String,
    onStop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FlowerMessage(
            color = Color(0xFF424966),
            message = "오늘도 좋은\n 하루 보내!"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text =
                if (detectionText.startsWith("데이터 수집")) {
                    detectionText
                } else {
                    "감지 측정 중"
                },
            color = Color(0xFFB8C0CC),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

        Text(
            text =
                if (serverStatus.contains("실패")) {
                    "기록 전송 확인 필요"
                } else {
                    "자동 기록 중"
                },
            color =
                if (serverStatus.contains("실패")) {
                    Color(0xFFFF8A80)
                } else {
                    Color(0xFF9AA4B2)
                },
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(6.dp))

        Button(
            onClick = onStop,
            modifier = Modifier
                .width(112.dp)
                .height(42.dp)
        ) {
            Text(
                text = "감지 멈춤",
                color = Color.Black
            )
        }
    }
}

@Composable
private fun WarningScreen(
    onStop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FlowerMessage(
            color = Color(0xFF31593C),
            message = "잠깐만\n손을 쉬어보자!"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "긁음이 감지됐어요",
            color = Color(0xFFFFC7C7),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Button(
            onClick = onStop,
            modifier = Modifier
                .width(112.dp)
                .height(42.dp)
        ) {
            Text(
                text = "감지 멈춤",
                color = Color.Black
            )
        }
    }
}

@Composable
private fun FinishedScreen(
    onRestart: () -> Unit,
    onHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FlowerMessage(
            color = Color(0xFF60384F),
            message = "정말 멋져!\n피부가 편안해질 거야!"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onHome,
                modifier = Modifier
                    .width(82.dp)
                    .height(40.dp)
            ) {
                Text(
                    text = "홈",
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onRestart,
                modifier = Modifier
                    .width(96.dp)
                    .height(40.dp)
            ) {
                Text(
                    text = "다시 시작",
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun FlowerMessage(
    color: Color,
    message: String
) {
    Box(
        modifier = Modifier.size(180.dp),
        contentAlignment = Alignment.Center
    ) {
        val petalSize = 72.dp

        Box(
            modifier = Modifier
                .size(petalSize)
                .offset(y = (-48).dp)
                .background(color, CircleShape)
        )

        Box(
            modifier = Modifier
                .size(petalSize)
                .offset(x = 42.dp, y = (-24).dp)
                .background(color, CircleShape)
        )

        Box(
            modifier = Modifier
                .size(petalSize)
                .offset(x = 42.dp, y = 24.dp)
                .background(color, CircleShape)
        )

        Box(
            modifier = Modifier
                .size(petalSize)
                .offset(y = 48.dp)
                .background(color, CircleShape)
        )

        Box(
            modifier = Modifier
                .size(petalSize)
                .offset(x = (-42).dp, y = 24.dp)
                .background(color, CircleShape)
        )

        Box(
            modifier = Modifier
                .size(petalSize)
                .offset(x = (-42).dp, y = (-24).dp)
                .background(color, CircleShape)
        )

        Box(
            modifier = Modifier
                .size(108.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MascotFace() {
    Image(
        painter = painterResource(
            id = R.drawable.atocue_character
        ),
        contentDescription = "AtoCue 캐릭터",
        modifier = Modifier.size(130.dp),
        contentScale = ContentScale.Fit
    )
}


suspend fun sendDetectedScratchEvent(
    probability: Float,
    detectedAtMillis: Long
): String {
    val endTime =
        Instant.ofEpochMilli(detectedAtMillis)

    /*
     * 현재 모델은 2초 센서 윈도우를 분석하므로
     * 감지 시각으로부터 2초 전을 시작 시각으로 사용한다.
     */
    val startTime =
        endTime.minusSeconds(2)

    /*
     * 확률을 1~5 단계 강도로 단순 변환한다.
     * 현재는 해커톤용 임시 규칙이며,
     * 추후 실제 움직임 강도 기반으로 바꿀 수 있다.
     */
    val intensity =
        when {
            probability >= 0.9f -> 5
            probability >= 0.8f -> 4
            probability >= 0.7f -> 3
            probability >= 0.6f -> 2
            else -> 1
        }

    val request =
        ScratchIngestRequest(
            deviceId = 1L,
            modelVersion = "scratch-binary-v1",
            calibrationVersion = 1,
            schemaVersion = 1,
            events = listOf(
                ScratchEventRequest(
                    clientEventId =
                        UUID.randomUUID().toString(),
                    startTs = startTime.toString(),
                    endTs = endTime.toString(),
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
        RetrofitClient.scratchApi.sendScratchEvents(
            userId = 1L,
            idempotencyKey =
                UUID.randomUUID().toString(),
            backfill = false,
            request = request
        )

    if (!response.isSuccessful) {
        val errorBody =
            response.errorBody()?.string()

        throw IllegalStateException(
            "서버 응답 ${response.code()}: $errorBody"
        )
    }

    return "자동 저장 성공: ${response.code()}"
}

/*
 * Python preprocess.py의 extract_axis_features와 동일한 특징을 계산한다.
 */
fun extractFeatures(
    samples: List<SensorSample>
): Map<String, Float> {
    val valuesByColumn = linkedMapOf(
        "acc_x" to samples.map { it.accX },
        "acc_y" to samples.map { it.accY },
        "acc_z" to samples.map { it.accZ },
        "gyro_x" to samples.map { it.gyroX },
        "gyro_y" to samples.map { it.gyroY },
        "gyro_z" to samples.map { it.gyroZ }
    )

    val features =
        linkedMapOf<String, Float>()

    valuesByColumn.forEach {
            (columnName, values) ->

        val minimum =
            values.minOrNull() ?: 0f

        val maximum =
            values.maxOrNull() ?: 0f

        features["${columnName}_mean"] =
            calculateMean(values)

        features["${columnName}_std"] =
            calculatePopulationStandardDeviation(
                values
            )

        features["${columnName}_min"] =
            minimum

        features["${columnName}_max"] =
            maximum

        features["${columnName}_range"] =
            maximum - minimum

        features["${columnName}_median"] =
            calculateMedian(values)

        features["${columnName}_energy"] =
            calculateEnergy(values)
    }

    val accMagnitude =
        samples.map { sample ->
            sqrt(
                sample.accX * sample.accX +
                        sample.accY * sample.accY +
                        sample.accZ * sample.accZ
            )
        }

    val gyroMagnitude =
        samples.map { sample ->
            sqrt(
                sample.gyroX * sample.gyroX +
                        sample.gyroY * sample.gyroY +
                        sample.gyroZ * sample.gyroZ
            )
        }

    features["acc_magnitude_mean"] =
        calculateMean(accMagnitude)

    features["acc_magnitude_std"] =
        calculatePopulationStandardDeviation(
            accMagnitude
        )

    features["acc_magnitude_max"] =
        accMagnitude.maxOrNull() ?: 0f

    features["acc_magnitude_energy"] =
        calculateEnergy(accMagnitude)

    features["gyro_magnitude_mean"] =
        calculateMean(gyroMagnitude)

    features["gyro_magnitude_std"] =
        calculatePopulationStandardDeviation(
            gyroMagnitude
        )

    features["gyro_magnitude_max"] =
        gyroMagnitude.maxOrNull() ?: 0f

    features["gyro_magnitude_energy"] =
        calculateEnergy(gyroMagnitude)

    return features
}

fun createOrderedFeatureArray(
    featureMap: Map<String, Float>,
    featureColumns: List<String>
): FloatArray {
    return FloatArray(featureColumns.size) {
            index ->

        val featureName =
            featureColumns[index]

        featureMap[featureName]
            ?: throw IllegalArgumentException(
                "계산되지 않은 특징입니다: $featureName"
            )
    }
}

fun normalizeFeatures(
    features: FloatArray,
    means: FloatArray,
    scales: FloatArray
): FloatArray {
    require(features.size == means.size) {
        "특징과 평균 배열의 크기가 다릅니다."
    }

    require(features.size == scales.size) {
        "특징과 표준편차 배열의 크기가 다릅니다."
    }

    return FloatArray(features.size) {
            index ->

        val scale =
            if (scales[index] == 0f) {
                1f
            } else {
                scales[index]
            }

        (
                features[index] -
                        means[index]
                ) / scale
    }
}

fun runInference(
    interpreter: Interpreter,
    input: FloatArray
): Float {
    val inputArray =
        arrayOf(input)

    val outputArray =
        Array(1) {
            FloatArray(1)
        }

    interpreter.run(
        inputArray,
        outputArray
    )

    return outputArray[0][0]
}

fun calculateMean(
    values: List<Float>
): Float {
    if (values.isEmpty()) {
        return 0f
    }

    return values.sum() / values.size
}

/*
 * Python의 std(ddof=0)과 동일한 모집단 표준편차다.
 */
fun calculatePopulationStandardDeviation(
    values: List<Float>
): Float {
    if (values.isEmpty()) {
        return 0f
    }

    val mean =
        calculateMean(values)

    val variance =
        values.sumOf { value ->
            val difference =
                value.toDouble() -
                        mean.toDouble()

            difference * difference
        } / values.size.toDouble()

    return sqrt(variance).toFloat()
}

fun calculateMedian(
    values: List<Float>
): Float {
    if (values.isEmpty()) {
        return 0f
    }

    val sorted =
        values.sorted()

    val middle =
        sorted.size / 2

    return if (sorted.size % 2 == 0) {
        (
                sorted[middle - 1] +
                        sorted[middle]
                ) / 2f
    } else {
        sorted[middle]
    }
}

fun calculateEnergy(
    values: List<Float>
): Float {
    if (values.isEmpty()) {
        return 0f
    }

    val sumOfSquares =
        values.sumOf { value ->
            val doubleValue =
                value.toDouble()

            doubleValue * doubleValue
        }

    return (
            sumOfSquares /
                    values.size.toDouble()
            ).toFloat()
}
fun loadModelFile(
    context: Context,
    modelName: String
): ByteBuffer {
    val modelBytes =
        context.assets
            .open(modelName)
            .use { inputStream ->
                inputStream.readBytes()
            }

    return ByteBuffer
        .allocateDirect(modelBytes.size)
        .apply {
            order(ByteOrder.nativeOrder())
            put(modelBytes)
            rewind()
        }
}

fun loadModelConfig(
    context: Context,
    configName: String
): ModelConfig {
    val jsonText =
        context.assets
            .open(configName)
            .bufferedReader()
            .use { reader ->
                reader.readText()
            }

    val jsonObject =
        JSONObject(jsonText)

    val featureColumnsJson =
        jsonObject.getJSONArray(
            "feature_columns"
        )

    val meansJson =
        jsonObject.getJSONArray("mean")

    val scalesJson =
        jsonObject.getJSONArray("scale")

    val featureColumns =
        List(featureColumnsJson.length()) {
                index ->
            featureColumnsJson.getString(index)
        }

    val means =
        FloatArray(meansJson.length()) {
                index ->
            meansJson
                .getDouble(index)
                .toFloat()
        }

    val scales =
        FloatArray(scalesJson.length()) {
                index ->
            scalesJson
                .getDouble(index)
                .toFloat()
        }

    val threshold =
        jsonObject
            .optDouble(
                "threshold",
                0.5
            )
            .toFloat()

    val windowSize =
        jsonObject.optInt(
            "window_size",
            DEFAULT_WINDOW_SIZE
        )

    val stepSize =
        jsonObject.optInt(
            "step_size",
            DEFAULT_STEP_SIZE
        )

    require(
        featureColumns.size ==
                means.size
    ) {
        "특징 이름과 평균 배열의 크기가 다릅니다."
    }

    require(
        featureColumns.size ==
                scales.size
    ) {
        "특징 이름과 표준편차 배열의 크기가 다릅니다."
    }

    return ModelConfig(
        featureColumns = featureColumns,
        means = means,
        scales = scales,
        threshold = threshold,
        windowSize = windowSize,
        stepSize = stepSize
    )
}

fun vibrateWatch(
    context: Context
) {
    try {
        val vibrator: Vibrator =
            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.S
            ) {
                val vibratorManager =
                    context.getSystemService(
                        Context.VIBRATOR_MANAGER_SERVICE
                    ) as VibratorManager

                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(
                    Context.VIBRATOR_SERVICE
                ) as Vibrator
            }

        if (!vibrator.hasVibrator()) {
            return
        }

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    300L,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(250L)
        }
    } catch (exception: Exception) {
        Log.e(
            "VIBRATION",
            "Vibration failed",
            exception
        )
    }
}