package com.example.scratchdetecter.detection

class SensorBuffer {

    companion object {
        private const val WINDOW_SIZE = 100
        private const val STEP_SIZE = 50
    }

    private val samples = mutableListOf<SensorSample>()

    /**
     * 센서 샘플 하나를 버퍼에 추가한다.
     *
     * 100개가 모이면 모델 입력용 윈도우를 반환하고,
     * 아직 부족하면 null을 반환한다.
     */
    fun add(sample: SensorSample): List<SensorSample>? {
        samples.add(sample)

        if (samples.size < WINDOW_SIZE) {
            return null
        }

        // 현재 앞쪽 100개를 하나의 추론 윈도우로 복사한다.
        val window = samples
            .take(WINDOW_SIZE)
            .toList()

        // 50개만 제거하여 다음 윈도우와 50개가 겹치도록 한다.
        repeat(STEP_SIZE) {
            samples.removeAt(0)
        }

        return window
    }

    fun size(): Int {
        return samples.size
    }

    fun clear() {
        samples.clear()
    }
}