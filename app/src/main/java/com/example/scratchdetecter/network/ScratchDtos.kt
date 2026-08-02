package com.example.scratchdetecter.network

data class ScratchIngestRequest(
    val deviceId: Long,
    val modelVersion: String,
    val calibrationVersion: Int,
    val schemaVersion: Int,
    val events: List<ScratchEventRequest>,
    val wearSecondsInBatch: Long
)

data class ScratchEventRequest(
    val clientEventId: String,
    val startTs: String,
    val endTs: String,
    val durationSec: Double,
    val intensity: Int,
    val confidence: Double,
    val windowCount: Int,
    val wearPosition: String
)

data class ScratchIngestResponse(
    val accepted: Int,
    val duplicated: Int,
    val rejected: List<RejectedEvent>,
    val serverWatermarkTs: String?
)

data class RejectedEvent(
    val clientEventId: String,
    val reason: String
)