package com.unique.schedify.core.presentation.common_composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import com.unique.schedify.core.presentation.utils.size_units.dp1
import com.unique.schedify.core.presentation.utils.size_units.dp12
import com.unique.schedify.core.presentation.utils.size_units.dp2
import com.unique.schedify.core.presentation.utils.size_units.dp8

@Composable
fun DashedDivider(
    vhSpace: Dp = dp12,
    dividerHeight: Dp = dp1,
    dividerColor: Color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
) {
    Column {
        Spacer(modifier = Modifier.height(vhSpace))
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(dividerHeight)
            .padding(vertical = dp8)
        ) {
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            drawLine(
                color = dividerColor,
                strokeWidth = dp2.toPx(),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                pathEffect = pathEffect
            )
        }
        Spacer(modifier = Modifier.height(vhSpace))
    }
}