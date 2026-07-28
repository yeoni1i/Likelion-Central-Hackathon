package com.example.scratchdetecter.presentation

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
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.example.scratchdetecter.presentation.theme.ScratchDetecterTheme
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sqrt

private const val MODEL_FILE_NAME =
    "scratch_binary_model.tflite"

private const val CONFIG_FILE_NAME =
    "scratch_binary_config.json"

private const val SAMPLE_INTERVAL_MS = 20L
private const val DEFAULT_WINDOW_SIZE = 100
private const val DEFAULT_STEP_SIZE = 50
private const val VIBRATION_COOLDOWN_MS = 2_000L

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


data class RecordingActivity(
    val fileLabel: String,
    val binaryLabel: String,
    val displayName: String
)

private val RECORDING_ACTIVITIES = listOf(
    RecordingActivity("normal_sitting", "normal", "앉아 있기"),
    RecordingActivity("normal_walking", "normal", "걷기"),
    RecordingActivity("normal_typing", "normal", "타이핑"),
    RecordingActivity("normal_phone", "normal", "휴대폰 사용"),
    RecordingActivity("normal_touch_face", "normal", "얼굴 만지기"),
    RecordingActivity("scratch_arm", "scratch", "팔 긁기"),
    RecordingActivity("scratch_neck", "scratch", "목 긁기"),
    RecordingActivity("scratch_leg", "scratch", "다리 긁기"),
    RecordingActivity("scratch_face", "scratch", "얼굴 긁기")
)

class MainActivity : ComponentActivity() {

    private var interpreter: Interpreter? = null
    private var modelConfig: ModelConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var modelStatus = "모델 확인 중"
        var inputShapeText = "-"
        var outputShapeText = "-"

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

            val inputShape =
                interpreter
                    ?.getInputTensor(0)
                    ?.shape()

            val outputShape =
                interpreter
                    ?.getOutputTensor(0)
                    ?.shape()

            inputShapeText =
                inputShape?.contentToString()
                    ?: "확인 실패"

            outputShapeText =
                outputShape?.contentToString()
                    ?: "확인 실패"

            val featureCount =
                modelConfig?.featureColumns?.size ?: 0

            val modelInputCount =
                inputShape?.lastOrNull() ?: 0

            if (featureCount != modelInputCount) {
                throw IllegalStateException(
                    "모델 입력 개수($modelInputCount)와 " +
                            "설정 특징 개수($featureCount)가 다릅니다."
                )
            }

            modelStatus = "모델 준비 완료"

            Log.d(
                "TFLITE",
                "Model loaded successfully"
            )

            Log.d(
                "TFLITE",
                "Input shape: $inputShapeText"
            )

            Log.d(
                "TFLITE",
                "Output shape: $outputShapeText"
            )

            Log.d(
                "TFLITE",
                "Feature count: $featureCount"
            )

            Log.d(
                "TFLITE",
                "Threshold: ${modelConfig?.threshold}"
            )
        } catch (exception: Exception) {
            modelStatus = "모델 또는 설정 로드 실패"

            Log.e(
                "TFLITE",
                "Failed to prepare model",
                exception
            )
        }

        setContent {
            ScratchDetecterTheme {
                SensorRecordingScreen(
                    interpreter = interpreter,
                    modelConfig = modelConfig,
                    modelStatus = modelStatus,
                    inputShapeText = inputShapeText,
                    outputShapeText = outputShapeText
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
fun SensorRecordingScreen(
    interpreter: Interpreter?,
    modelConfig: ModelConfig?,
    modelStatus: String,
    inputShapeText: String,
    outputShapeText: String
) {
    val context = LocalContext.current

    var accelerometerX by remember {
        mutableFloatStateOf(0f)
    }

    var accelerometerY by remember {
        mutableFloatStateOf(0f)
    }

    var accelerometerZ by remember {
        mutableFloatStateOf(0f)
    }

    var gyroscopeX by remember {
        mutableFloatStateOf(0f)
    }

    var gyroscopeY by remember {
        mutableFloatStateOf(0f)
    }

    var gyroscopeZ by remember {
        mutableFloatStateOf(0f)
    }

    var selectedActivityIndex by remember {
        mutableIntStateOf(0)
    }

    val selectedActivity =
        RECORDING_ACTIVITIES[selectedActivityIndex]

    var isRecording by remember {
        mutableStateOf(false)
    }

    var savedFilePath by remember {
        mutableStateOf("")
    }

    var sampleCount by remember {
        mutableIntStateOf(0)
    }

    var predictionCount by remember {
        mutableIntStateOf(0)
    }

    var scratchProbability by remember {
        mutableFloatStateOf(0f)
    }

    var detectionText by remember {
        mutableStateOf("감지 대기")
    }

    var csvWriter by remember {
        mutableStateOf<BufferedWriter?>(null)
    }

    DisposableEffect(
        context,
        interpreter,
        modelConfig
    ) {
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

        val sampleBuffer =
            mutableListOf<SensorSample>()

        val windowSize =
            modelConfig?.windowSize
                ?: DEFAULT_WINDOW_SIZE

        val stepSize =
            modelConfig?.stepSize
                ?: DEFAULT_STEP_SIZE

        var latestAccX = 0f
        var latestAccY = 0f
        var latestAccZ = 0f

        var latestGyroX = 0f
        var latestGyroY = 0f
        var latestGyroZ = 0f

        var lastSampleTime = 0L
        var lastVibrationTime = 0L

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

                            accelerometerX = latestAccX
                            accelerometerY = latestAccY
                            accelerometerZ = latestAccZ
                        }

                        Sensor.TYPE_GYROSCOPE -> {
                            latestGyroX = event.values[0]
                            latestGyroY = event.values[1]
                            latestGyroZ = event.values[2]

                            gyroscopeX = latestGyroX
                            gyroscopeY = latestGyroY
                            gyroscopeZ = latestGyroZ
                        }
                    }

                    val currentTime =
                        System.currentTimeMillis()

                    /*
                     * 센서 이벤트가 들어올 때마다 저장하지 않고,
                     * 약 20ms마다 현재 최신 센서값으로 샘플 하나를 만든다.
                     */
                    if (
                        currentTime - lastSampleTime
                        < SAMPLE_INTERVAL_MS
                    ) {
                        return
                    }

                    lastSampleTime = currentTime

                    val sample = SensorSample(
                        accX = latestAccX,
                        accY = latestAccY,
                        accZ = latestAccZ,
                        gyroX = latestGyroX,
                        gyroY = latestGyroY,
                        gyroZ = latestGyroZ
                    )

                    /*
                     * CSV 기록 기능
                     */
                    if (isRecording) {
                        val line = buildString {
                            append(currentTime)
                            append(",")

                            append(sample.accX)
                            append(",")

                            append(sample.accY)
                            append(",")

                            append(sample.accZ)
                            append(",")

                            append(sample.gyroX)
                            append(",")

                            append(sample.gyroY)
                            append(",")

                            append(sample.gyroZ)
                            append(",")

                            append(selectedActivity.binaryLabel)
                            append("\n")
                        }

                        try {
                            csvWriter?.write(line)

                            /*
                             * 매 행마다 flush하면 기록이 안정적이지만
                             * 배터리와 저장장치 사용량은 증가할 수 있다.
                             */
                            csvWriter?.flush()

                            sampleCount += 1
                        } catch (exception: Exception) {
                            Log.e(
                                "CSV",
                                "CSV 저장 실패",
                                exception
                            )
                        }
                    }

                    /*
                     * 실시간 추론용 버퍼
                     */
                    if (
                        interpreter == null ||
                        modelConfig == null
                    ) {
                        return
                    }

                    sampleBuffer.add(sample)

                    if (sampleBuffer.size < windowSize) {
                        detectionText =
                            "데이터 수집 ${sampleBuffer.size}/$windowSize"

                        return
                    }

                    try {
                        val window =
                            sampleBuffer
                                .take(windowSize)

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
                        predictionCount += 1

                        val isScratch =
                            probability >=
                                    modelConfig.threshold

                        detectionText =
                            if (isScratch) {
                                "긁기 감지"
                            } else {
                                "일상 동작"
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
                        }
                    } catch (exception: Exception) {
                        detectionText = "예측 오류"

                        Log.e(
                            "PREDICTION",
                            "Prediction failed",
                            exception
                        )
                    }

                    /*
                     * 100개를 모두 버리지 않고 앞의 50개만 제거한다.
                     * 다음 윈도우는 이전 윈도우와 1초 구간이 겹친다.
                     */
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

            try {
                csvWriter?.flush()
                csvWriter?.close()
            } catch (exception: Exception) {
                Log.e(
                    "CSV",
                    "CSV 종료 실패",
                    exception
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.Center
    ) {
        Text(
            text = "데이터 수집",
            style =
                MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = selectedActivity.displayName,
            style =
                MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Text(
            text = "라벨: ${selectedActivity.binaryLabel}",
            style =
                MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (!isRecording) {
                        selectedActivityIndex =
                            if (selectedActivityIndex == 0) {
                                RECORDING_ACTIVITIES.lastIndex
                            } else {
                                selectedActivityIndex - 1
                            }
                    }
                }
            ) {
                Text("이전")
            }

            Button(
                onClick = {
                    if (!isRecording) {
                        selectedActivityIndex =
                            if (
                                selectedActivityIndex ==
                                RECORDING_ACTIVITIES.lastIndex
                            ) {
                                0
                            } else {
                                selectedActivityIndex + 1
                            }
                    }
                }
            ) {
                Text("다음")
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            onClick = {
                if (!isRecording) {
                    val dateFormat =
                        SimpleDateFormat(
                            "yyyyMMdd_HHmmss",
                            Locale.getDefault()
                        )

                    val fileName =
                        "${selectedActivity.fileLabel}_" +
                                "${dateFormat.format(Date())}.csv"

                    val folder =
                        context.getExternalFilesDir(null)

                    if (folder == null) {
                        savedFilePath =
                            "저장 폴더 확인 실패"

                        return@Button
                    }

                    val file =
                        File(folder, fileName)

                    try {
                        csvWriter =
                            BufferedWriter(
                                FileWriter(file)
                            )

                        csvWriter?.write(
                            "timestamp," +
                                    "acc_x,acc_y,acc_z," +
                                    "gyro_x,gyro_y,gyro_z," +
                                    "label\n"
                        )

                        csvWriter?.flush()

                        savedFilePath =
                            file.absolutePath

                        sampleCount = 0
                        isRecording = true

                        Log.d(
                            "CSV",
                            "Recording started: " +
                                    file.absolutePath
                        )
                    } catch (exception: Exception) {
                        Log.e(
                            "CSV",
                            "파일 생성 실패",
                            exception
                        )

                        savedFilePath =
                            "파일 생성 실패"
                    }
                } else {
                    try {
                        csvWriter?.flush()
                        csvWriter?.close()
                    } catch (exception: Exception) {
                        Log.e(
                            "CSV",
                            "파일 종료 실패",
                            exception
                        )
                    }

                    csvWriter = null
                    isRecording = false
                }
            }
        ) {
            Text(
                if (isRecording) {
                    "기록 종료"
                } else {
                    "기록 시작"
                }
            )
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text =
                if (isRecording) {
                    "${sampleCount}개 기록 중"
                } else {
                    "기록 대기"
                },
            style =
                MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

        if (savedFilePath.isNotEmpty()) {
            Text(
                text =
                    when (savedFilePath) {
                        "파일 생성 실패",
                        "저장 폴더 확인 실패" ->
                            savedFilePath

                        else ->
                            "CSV 저장됨"
                    },
                style =
                    MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
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

fun formatSensorValue(
    value: Float
): String {
    return String.format(
        Locale.US,
        "%.2f",
        value
    )
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
                    250L,
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