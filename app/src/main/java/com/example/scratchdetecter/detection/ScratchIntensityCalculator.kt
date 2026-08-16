package com.example.scratchdetecter.detection

import kotlin.math.abs
import kotlin.math.sqrt

object ScratchIntensityCalculator {

    // 일단 실행용 임시 threshold
    // 나중에 실제 RMS 데이터 수집 후 수정
    private const val MID_THRESHOLD = 3.0f
    private const val HIGH_THRESHOLD = 5.0f
    private const val VERY_HIGH_THRESHOLD = 9.0f

    /**
     * 1개 AI window의 동적 가속도 RMS 계산
     */
    fun calculateRms(
        samples: List<SensorSample>
    ): Float {

        if (samples.isEmpty()) {
            return 0f
        }

        val dynamicMagnitudes =
            samples.map { sample ->

                val magnitude =
                    sqrt(
                        sample.accX * sample.accX +
                                sample.accY * sample.accY +
                                sample.accZ * sample.accZ
                    )

                // 중력가속도 제거
                abs(magnitude - 9.81f)
            }

        val meanSquare =
            dynamicMagnitudes
                .map { value ->
                    value * value
                }
                .average()

        return sqrt(meanSquare).toFloat()
    }

    /**
     * RMS → intensity 1~5 변환
     */
    fun calculate(
        samples: List<SensorSample>
    ): Int {

        val accelRms =
            calculateRms(samples)

        return when {
            accelRms >= 8.0f -> 4   // 매우 강함
            accelRms >= 5.0f -> 3   // 강함
            accelRms >= 3.0f -> 2   // 보통
            else -> 1              // 약함
        }
    }
}