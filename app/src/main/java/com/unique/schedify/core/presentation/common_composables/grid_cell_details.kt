package com.unique.schedify.core.presentation.common_composables

import androidx.compose.ui.graphics.Color
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens

data class CellUiDetails(
    val text: String,
    val screen: AvailableScreens,
    val image: Int? = null,
    val imageUrl: String? = null,
    val description: String? = null,
    val backgroundCardGradientColors: List<Color>? = null,
    val titleColor: Color? = null,
)
