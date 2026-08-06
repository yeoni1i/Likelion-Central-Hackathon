package com.example.scratchdetecter.detection

const val MODEL_FILE_NAME = "scratch_binary_model.tflite"
const val CONFIG_FILE_NAME = "scratch_binary_config.json"

const val SAMPLE_INTERVAL_MS = 20L
const val DEFAULT_WINDOW_SIZE = 100
const val DEFAULT_STEP_SIZE = 50

const val SCRATCH_WARNING_THRESHOLD_MS = 3_000L
const val WARNING_DISPLAY_MS = 3_000L
const val FINISHED_DISPLAY_MS = 3_000L
const val WARNING_COOLDOWN_MS = 10_000L
const val SERVER_SEND_COOLDOWN_MS = 5_000L
const val LOW_BATTERY_PERCENT = 15

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
