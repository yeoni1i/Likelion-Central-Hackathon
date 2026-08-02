package com.example.atocuemobile.network.dto


data class ScratchTimelineResponse(
    val date: String,
    val events: List<ScratchTimelineItem>
)
