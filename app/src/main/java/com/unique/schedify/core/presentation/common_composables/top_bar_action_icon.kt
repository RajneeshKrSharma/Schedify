package com.unique.schedify.core.presentation.common_composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.unique.schedify.core.presentation.utils.size_units.dp1
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp24
import com.unique.schedify.core.presentation.utils.size_units.dp8


@Composable
fun ActionIcons(
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.secondary,
    iconText: String,
    borderStrokeColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(dp24))
            .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
            .border(
                BorderStroke(dp1, borderStrokeColor),
                shape = RoundedCornerShape(dp24)
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = dp16, vertical = dp8),
            text = iconText,
            style = MaterialTheme.typography.labelMedium.copy(color = textColor)
        )
    }
}