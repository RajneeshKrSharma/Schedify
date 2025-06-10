package com.unique.schedify.core.presentation.utils.ui_utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val baseBgGradient = Brush.verticalGradient(
    colorStops = arrayOf(
        0.0f to Color(0xFFFFFFFF),
        0.3f to Color(0xFFFFFFFF),
        1.0f to Color(0xFF8136FF)
    )
)