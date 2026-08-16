package com.example.atocuemobile.ui.screen.timeline.model

import androidx.compose.ui.graphics.Color
import com.example.atocuemobile.R
import com.example.atocuemobile.ui.screen.timeline.LevelCaution
import com.example.atocuemobile.ui.screen.timeline.LevelDanger
import com.example.atocuemobile.ui.screen.timeline.LevelNormal
import com.example.atocuemobile.ui.screen.timeline.LevelSevere
import com.example.atocuemobile.ui.screen.timeline.LevelStable

enum class ScratchLevel(val label: String, val color: Color, val iconRes: Int) {
    STABLE("안정", LevelStable, R.drawable.scratch_level_stable),
    NORMAL("보통", LevelNormal, R.drawable.scratch_level_normal),
    CAUTION("주의", LevelCaution, R.drawable.scratch_level_caution),
    DANGER("위험", LevelDanger, R.drawable.scratch_level_danger),
    SEVERE("매우 위험", LevelSevere, R.drawable.scratch_level_severe)
}