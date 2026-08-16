package com.example.scratchdetecter.detection

import android.content.Context
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.io.Closeable
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ScratchDetector(
    context: Context
) : Closeable {

    private val config: ModelConfig = loadConfig(context)
    private val interpreter: Interpreter = Interpreter(loadModel(context))

    init {
        val modelInputCount = interpreter.getInputTensor(0).shape().lastOrNull() ?: 0
        require(modelInputCount == config.featureColumns.size) {
            "모델 입력 개수($modelInputCount)와 설정 특징 개수(${config.featureColumns.size})가 다릅니다."
        }
    }

    val windowSize: Int get() = config.windowSize
    val stepSize: Int get() = config.stepSize
    val threshold: Float get() = config.threshold

    fun predict(samples: List<SensorSample>): Float {
        require(samples.size >= config.windowSize) {
            "추론에는 최소 ${config.windowSize}개의 샘플이 필요합니다."
        }

        val featureMap = FeatureExtractor.extract(samples.take(config.windowSize))
        val normalized = FeatureExtractor.orderAndNormalize(featureMap, config)
        val output = Array(1) { FloatArray(1) }
        interpreter.run(arrayOf(normalized), output)
        return output[0][0]
    }

    fun isScratch(probability: Float): Boolean = probability >= config.threshold

    override fun close() {
        interpreter.close()
    }

    private fun loadModel(context: Context): ByteBuffer {
        val bytes = context.assets.open(MODEL_FILE_NAME).use { it.readBytes() }
        return ByteBuffer.allocateDirect(bytes.size).apply {
            order(ByteOrder.nativeOrder())
            put(bytes)
            rewind()
        }
    }

    private fun loadConfig(context: Context): ModelConfig {
        val json = context.assets.open(CONFIG_FILE_NAME)
            .bufferedReader()
            .use { JSONObject(it.readText()) }

        val featureColumnsJson = json.getJSONArray("feature_columns")
        val meansJson = json.getJSONArray("mean")
        val scalesJson = json.getJSONArray("scale")

        val featureColumns = List(featureColumnsJson.length()) { featureColumnsJson.getString(it) }
        val means = FloatArray(meansJson.length()) { meansJson.getDouble(it).toFloat() }
        val scales = FloatArray(scalesJson.length()) { scalesJson.getDouble(it).toFloat() }

        require(featureColumns.size == means.size && featureColumns.size == scales.size)

        return ModelConfig(
            featureColumns = featureColumns,
            means = means,
            scales = scales,
            threshold = json.optDouble("threshold", 0.5).toFloat(),
            windowSize = json.optInt("window_size", DEFAULT_WINDOW_SIZE),
            stepSize = json.optInt("step_size", DEFAULT_STEP_SIZE)
        )
    }
}
