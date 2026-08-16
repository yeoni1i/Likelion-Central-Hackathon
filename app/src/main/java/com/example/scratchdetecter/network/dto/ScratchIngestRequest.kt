package com.example.scratchdetecter.network.dto

data class ScratchIngestRequest(
    val deviceId: Long,
    val modelVersion: String,
    val calibrationVersion: Int,
    val schemaVersion: Int,
    val events: List<ScratchEventRequest>,
    val wearSecondsInBatch: Long
)
