package com.unique.schedify.core.presentation.common_composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.unique.schedify.R
import com.unique.schedify.core.presentation.utils.size_units.dp1
import com.unique.schedify.core.presentation.utils.size_units.dp12
import com.unique.schedify.core.presentation.utils.size_units.dp14
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp24
import com.unique.schedify.core.presentation.utils.size_units.dp4
import com.unique.schedify.core.presentation.utils.size_units.dp8


@Composable
fun ActionIcons(
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.secondary,
    iconText: String,
    icon: ImageVector? = null,
    iconImage: Painter? = null,
    iconColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
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
        Row(
            modifier = Modifier
                .padding(horizontal = dp16, vertical = dp8),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            iconImage?.let {
                Image(
                    modifier = Modifier
                        .width(dp14)
                        .height(dp14),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer),
                    painter = painterResource(R.drawable.add_collaborator),
                    contentDescription = iconText
                )
                Spacer(modifier = Modifier.width(dp4))
            }
            Text(
                text = iconText,
                style = MaterialTheme.typography.labelMedium.copy(color = textColor)
            )
            icon?.let {
                Spacer(modifier = Modifier.width(dp4))
                Icon(
                    icon, iconText,
                    modifier = Modifier
                        .size(dp12),
                    tint = iconColor
                )
            }
        }
    }
}