package com.example.scratchdetecter.MachineLearning

import android.content.Context
import org.json.JSONObject
import kotlin.math.sqrt

class FeatureExtractor(
    context: Context
) {

    private val featureNames: List<String>
    private val means: FloatArray
    private val scales: FloatArray

    init {
        val jsonText =
            context.assets
                .open("scratch_binary_config.json")
                .bufferedReader()
                .use { it.readText() }

        val json = JSONObject(jsonText)

        val featureArray =
            json.getJSONArray("feature_columns")

        val meanArray =
            json.getJSONArray("mean")

        val scaleArray =
            json.getJSONArray("scale")

        featureNames =
            List(featureArray.length()) { index ->
                featureArray.getString(index)
            }

        means =
            FloatArray(meanArray.length()) { index ->
                meanArray.getDouble(index).toFloat()
            }

        scales =
            FloatArray(scaleArray.length()) { index ->
                scaleArray.getDouble(index).toFloat()
            }

        require(featureNames.size == 50) {
            "특징 개수가 50개가 아닙니다: ${featureNames.size}"
        }

        require(means.size == featureNames.size) {
            "mean 개수와 feature 개수가 다릅니다."
        }

        require(scales.size == featureNames.size) {
            "scale 개수와 feature 개수가 다릅니다."
        }
    }


    fun extract(
        samples: List<SensorSample>
    ): FloatArray {

        require(samples.size == 100) {
            "센서 샘플은 정확히 100개가 필요합니다. 현재: ${samples.size}"
        }

        val rawFeatures =
            extractRawFeatures(samples)

        return FloatArray(featureNames.size) { index ->

            val featureName =
                featureNames[index]

            val rawValue =
                rawFeatures[featureName]
                    ?: error(
                        "계산되지 않은 특징입니다: $featureName"
                    )

            val scale =
                scales[index]

            if (scale == 0f) {
                0f
            } else {
                (rawValue - means[index]) / scale
            }
        }
    }

    /**
     * Python preprocess.py와 동일한 50개 특징을 계산한다.
     */
    private fun extractRawFeatures(
        samples: List<SensorSample>
    ): Map<String, Float> {

        val columns =
            linkedMapOf(
                "acc_x" to samples.map { it.accX },
                "acc_y" to samples.map { it.accY },
                "acc_z" to samples.map { it.accZ },
                "gyro_x" to samples.map { it.gyroX },
                "gyro_y" to samples.map { it.gyroY },
                "gyro_z" to samples.map { it.gyroZ }
            )

        val features =
            linkedMapOf<String, Float>()

        for ((columnName, values) in columns) {

            val minimum =
                values.minOrNull()
                    ?: error("$columnName 값이 없습니다.")

            val maximum =
                values.maxOrNull()
                    ?: error("$columnName 값이 없습니다.")

            features["${columnName}_mean"] =
                calculateMean(values)

            features["${columnName}_std"] =
                calculatePopulationStandardDeviation(values)

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

        val accelerationMagnitude =
            samples.map { sample ->
                sqrt(
                    sample.accX * sample.accX +
                            sample.accY * sample.accY +
                            sample.accZ * sample.accZ
                )
            }

        val gyroscopeMagnitude =
            samples.map { sample ->
                sqrt(
                    sample.gyroX * sample.gyroX +
                            sample.gyroY * sample.gyroY +
                            sample.gyroZ * sample.gyroZ
                )
            }

        addMagnitudeFeatures(
            prefix = "acc_magnitude",
            values = accelerationMagnitude,
            features = features
        )

        addMagnitudeFeatures(
            prefix = "gyro_magnitude",
            values = gyroscopeMagnitude,
            features = features
        )

        return features
    }

    private fun addMagnitudeFeatures(
        prefix: String,
        values: List<Float>,
        features: MutableMap<String, Float>
    ) {
        features["${prefix}_mean"] =
            calculateMean(values)

        features["${prefix}_std"] =
            calculatePopulationStandardDeviation(values)

        features["${prefix}_max"] =
            values.maxOrNull()
                ?: error("$prefix 값이 없습니다.")

        features["${prefix}_energy"] =
            calculateEnergy(values)
    }

    private fun calculateMean(
        values: List<Float>
    ): Float {
        return values.sum() / values.size
    }

    private fun calculatePopulationStandardDeviation(
        values: List<Float>
    ): Float {

        val mean =
            calculateMean(values)

        val variance =
            values.sumOf { value ->
                val difference =
                    value.toDouble() - mean.toDouble()

                difference * difference
            } / values.size

        return sqrt(variance).toFloat()
    }

    private fun calculateMedian(
        values: List<Float>
    ): Float {

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


    private fun calculateEnergy(
        values: List<Float>
    ): Float {

        val sumOfSquares =
            values.sumOf { value ->
                val number =
                    value.toDouble()

                number * number
            }

        return (
                sumOfSquares / values.size
                ).toFloat()
    }
}