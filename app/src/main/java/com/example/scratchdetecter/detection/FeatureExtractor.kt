package com.example.scratchdetecter.detection

import kotlin.math.sqrt

object FeatureExtractor {

    fun extract(
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

        val features = linkedMapOf<String, Float>()

        valuesByColumn.forEach { (columnName, values) ->
            val minimum = values.minOrNull() ?: 0f
            val maximum = values.maxOrNull() ?: 0f

            features["${columnName}_mean"] =
                mean(values)

            features["${columnName}_std"] =
                populationStd(values)

            features["${columnName}_min"] =
                minimum

            features["${columnName}_max"] =
                maximum

            features["${columnName}_range"] =
                maximum - minimum

            features["${columnName}_median"] =
                median(values)

            features["${columnName}_energy"] =
                energy(values)
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
            mean(accMagnitude)

        features["acc_magnitude_std"] =
            populationStd(accMagnitude)

        features["acc_magnitude_max"] =
            accMagnitude.maxOrNull() ?: 0f

        features["acc_magnitude_energy"] =
            energy(accMagnitude)

        features["gyro_magnitude_mean"] =
            mean(gyroMagnitude)

        features["gyro_magnitude_std"] =
            populationStd(gyroMagnitude)

        features["gyro_magnitude_max"] =
            gyroMagnitude.maxOrNull() ?: 0f

        features["gyro_magnitude_energy"] =
            energy(gyroMagnitude)

        return features
    }

    fun orderAndNormalize(
        featureMap: Map<String, Float>,
        config: ModelConfig
    ): FloatArray {
        require(
            config.featureColumns.size ==
                    config.means.size
        ) {
            "특징 이름과 평균 배열 크기가 다릅니다."
        }

        require(
            config.featureColumns.size ==
                    config.scales.size
        ) {
            "특징 이름과 표준편차 배열 크기가 다릅니다."
        }

        return FloatArray(
            config.featureColumns.size
        ) { index ->
            val featureName =
                config.featureColumns[index]

            val rawValue =
                featureMap[featureName]
                    ?: error(
                        "계산되지 않은 특징입니다: $featureName"
                    )

            val scale =
                if (config.scales[index] == 0f) {
                    1f
                } else {
                    config.scales[index]
                }

            (
                    rawValue -
                            config.means[index]
                    ) / scale
        }
    }

    private fun mean(
        values: List<Float>
    ): Float {
        if (values.isEmpty()) {
            return 0f
        }

        return values.sum() / values.size
    }

    private fun populationStd(
        values: List<Float>
    ): Float {
        if (values.isEmpty()) {
            return 0f
        }

        val average =
            mean(values)

        val variance =
            values.sumOf { value ->
                val difference =
                    value.toDouble() -
                            average.toDouble()

                difference * difference
            } / values.size.toDouble()

        return sqrt(variance).toFloat()
    }

    private fun median(
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

    private fun energy(
        values: List<Float>
    ): Float {
        if (values.isEmpty()) {
            return 0f
        }

        val sumOfSquares =
            values.sumOf { value ->
                val number =
                    value.toDouble()

                number * number
            }

        return (
                sumOfSquares /
                        values.size.toDouble()
                ).toFloat()
    }
}