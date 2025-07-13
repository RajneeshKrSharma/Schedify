package com.unique.schedify.core.presentation.common_composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.unique.schedify.core.presentation.utils.size_units.dp12
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp6
import com.unique.schedify.core.presentation.utils.size_units.dp8

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

@Composable
fun CircularGradientProgressBar(
    modifier: Modifier = Modifier,
    progress: Float,
    strokeWidth: Dp = dp12,
    backgroundColor: Color = MaterialTheme.colorScheme.onTertiary,
    gradientColors: List<Color> = listOf(
        MaterialTheme.colorScheme.surfaceBright,
        MaterialTheme.colorScheme.onSecondary,
        MaterialTheme.colorScheme.surfaceBright
    )
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500),
        label = "circularProgress"
    )

    val strokePx = with(LocalDensity.current) { strokeWidth.toPx() }
    val gradientBrush = Brush.sweepGradient(gradientColors)

    Canvas(modifier = modifier) {
        val diameter = size.minDimension
        val arcSize = Size(diameter - strokePx, diameter - strokePx)
        val topLeft = Offset(strokePx / 2, strokePx / 2)

        // Background Arc (circle)
        drawArc(
            color = backgroundColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokePx, cap = StrokeCap.Round)
        )

        // Foreground Progress Arc
        drawArc(
            brush = gradientBrush,
            startAngle = -90f,
            sweepAngle = 360f * animatedProgress,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokePx, cap = StrokeCap.Round)
        )
    }
}



