package com.example.atocuemobile.ui.screen.timeline.model

import com.example.atocuemobile.R

enum class SymptomType(val label: String, val iconRes: Int) {
    SEVERE_ITCH("심한 가려움증", R.drawable.symptom_severe_itch),
    RED_RASH("붉은 발진", R.drawable.symptom_red_rash),
    SCRATCH_WOUND("긁힌 상처", R.drawable.symptom_scratch_wound),
    OOZING_SCAB("진물과 딱지", R.drawable.symptom_oozing_scab),
    LICHENIFICATION("피부 태선화", R.drawable.symptom_lichenification),
    DRYNESS("건조증", R.drawable.symptom_dryness)
}