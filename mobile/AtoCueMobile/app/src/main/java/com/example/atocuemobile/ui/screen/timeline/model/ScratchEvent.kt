package com.example.atocuemobile.ui.screen.timeline.model

import java.time.LocalTime

data class ScratchEvent(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val durationSeconds: Int,
    val level: ScratchLevel
)