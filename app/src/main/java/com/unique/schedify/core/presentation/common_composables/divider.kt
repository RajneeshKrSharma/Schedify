package com.unique.schedify.core.presentation.common_composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import com.unique.schedify.core.presentation.utils.size_units.dp0
import com.unique.schedify.core.presentation.utils.size_units.dp1
import com.unique.schedify.core.presentation.utils.size_units.dp12
import com.unique.schedify.core.presentation.utils.size_units.dp2
import com.unique.schedify.core.presentation.utils.size_units.dp8

@Composable
fun DashedDivider(
    modifier: Modifier = Modifier,
    dividerHeight: Dp = dp1,
    vSpace: Dp = dp0, // Set default to 0 if vertical space not needed
    dividerColor: Color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
) {
    Box(
        modifier = modifier
            .padding(vertical = vSpace)
            .height(dividerHeight)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            drawLine(
                color = dividerColor,
                strokeWidth = dividerHeight.toPx(),
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                pathEffect = pathEffect
            )
        }
    }
}

@Composable
fun VerticalDashedDivider(
    modifier: Modifier = Modifier,
    horizontalSpace: Dp = dp12,
    dividerWidth: Dp = dp2,
    dividerColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(dividerWidth + horizontalSpace * 2) // total space
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.Center)
                .width(dividerWidth)
                .padding(horizontal = dp12)
        ) {
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            drawLine(
                color = dividerColor,
                strokeWidth = dividerWidth.toPx(), // match dividerWidth
                start = Offset(0f, 0f),
                end = Offset(0f, size.height),
                pathEffect = pathEffect
            )
        }
    }
}
