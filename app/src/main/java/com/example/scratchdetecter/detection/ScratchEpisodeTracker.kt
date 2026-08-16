package com.example.scratchdetecter.detection

/**
scratch window들을 하나의 긁음 episode로 묶는 역할
중간의 non-scratch가 짧으면 하나의 긁음으로 계속 이어서 처리 */

class ScratchEpisodeTracker(
    private val gracePeriodMs: Long = 2_000L
) {

    /**
     * 완성된 하나의 긁음 데이터
     *
     * 서버로 전송할 때 이 데이터를
     * ScratchEventRequest로 변환해서 사용한다.
     */
    data class ScratchEpisode(
        val startTimeMillis: Long,
        val endTimeMillis: Long,
        val durationSec: Double,
        val windowCount: Int,
        val averageConfidence: Double,
        val maxIntensity: Int
    )

    // 현재 긁음 episode가 진행 중인지
    private var scratching = false

    // 이번 긁음이 처음 시작된 시간
    private var scratchStartTime: Long? = null

    // 마지막으로 scratch 판정이 나온 시간
    private var lastScratchTime: Long? = null

    // 이번 긁음에서 scratch로 판정된 window 개수
    private var windowCount = 0

    // 평균 confidence 계산용 누적값
    private var confidenceSum = 0.0

    // 이번 긁음에서 가장 높은 intensity
    private var maxIntensity = 0

    fun onScratch(
        detectedAtMillis: Long,
        confidence: Double,
        intensity: Int
    ) {

        if (!scratching) {
            scratching = true

            scratchStartTime =
                detectedAtMillis - 2_000L

            windowCount = 0
            confidenceSum = 0.0
            maxIntensity = 0
        }

        // 가장 최근 scratch 시각 갱신
        lastScratchTime = detectedAtMillis

        // episode 통계 누적
        windowCount++

        confidenceSum += confidence

        maxIntensity =
            maxOf(
                maxIntensity,
                intensity
            )
    }

    /**
     * 마지막 scratch 이후 gracePeriodMs 이상
     * scratch가 나오지 않았다면 episode를 종료한다.
     *
     * 아직 grace period 안이라면 null 반환.
     */
    fun checkEpisodeEnd(
        nowMillis: Long
    ): ScratchEpisode? {

        if (!scratching) {
            return null
        }

        val lastScratch =
            lastScratchTime
                ?: return null

        val elapsed =
            nowMillis - lastScratch

        if (elapsed < gracePeriodMs) {
            return null
        }

        return finishEpisode()
    }

    /**
     * 사용자가 감지를 직접 종료하거나
     * 센서 측정이 종료될 때 사용한다.
     *
     * 진행 중인 긁음이 있으면 즉시 episode를 마감
     */
    fun forceFinish(): ScratchEpisode? {

        if (!scratching) {
            return null
        }

        return finishEpisode()
    }

    fun isScratching(): Boolean {
        return scratching
    }

    private fun finishEpisode(): ScratchEpisode? {

        val start =
            scratchStartTime
                ?: return null

        val lastScratch =
            lastScratchTime
                ?: return null

        if (windowCount <= 0) {
            reset()
            return null
        }

        val end = lastScratch

        val durationSec =
            (end - start)
                .coerceAtLeast(0L) /
                    1000.0

        val averageConfidence =
            confidenceSum /
                    windowCount.toDouble()

        val episode =
            ScratchEpisode(
                startTimeMillis = start,
                endTimeMillis = end,
                durationSec = durationSec,
                windowCount = windowCount,
                averageConfidence = averageConfidence,
                maxIntensity = maxIntensity
            )

        reset()

        return episode
    }

//저장후 내부 초기화
    private fun reset() {
        scratching = false

        scratchStartTime = null
        lastScratchTime = null

        windowCount = 0
        confidenceSum = 0.0
        maxIntensity = 0
    }
}