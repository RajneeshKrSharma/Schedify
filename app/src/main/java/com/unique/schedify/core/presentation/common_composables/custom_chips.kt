package com.unique.schedify.core.presentation.common_composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.unique.schedify.core.presentation.utils.size_units.dp1
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp4
import com.unique.schedify.core.presentation.utils.size_units.dp8

@Composable
fun CustomChip(
    text: String,
    textColor: Color,
    bgColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    borderColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    isOutlined: Boolean = false,
    outlinedBorderRadius: Dp = dp4,
    style: TextStyle = MaterialTheme.typography.bodySmall.copy(
        color = textColor
    )
) {

    if (isOutlined)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(outlinedBorderRadius))
                .background(color = bgColor)
                .border(
                    border = BorderStroke(width = dp1, color = borderColor),
                    shape = RoundedCornerShape(outlinedBorderRadius)
                )
        ) {
            Text(
                modifier = Modifier.padding(
                    horizontal = dp8, vertical = dp4
                ),
                text = text,
                style = style
            )
        }
    else

        Box(
            modifier = Modifier
                .background(
                    color = bgColor, shape = RoundedCornerShape(
                        dp16
                    )
                )
        ) {
            Text(
                modifier = Modifier.padding(
                    horizontal = dp8, vertical = dp4
                ),
                text = text,
                style = style
            )
        }
}

