package com.unique.schedify.core.presentation.common_composables

import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens

data class GridCellDetails(
    val screen: AvailableScreens,
    val image: Int,
    val text: String
)
