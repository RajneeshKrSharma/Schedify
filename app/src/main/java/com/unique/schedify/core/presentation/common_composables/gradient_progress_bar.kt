package com.unique.schedify.core.presentation.common_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import com.unique.schedify.core.presentation.utils.size_units.dp6

@Composable
fun GradientProgressBar(progress: Float) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onSecondary
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dp6)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.onTertiary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .background(brush = gradient)
        )
    }
}