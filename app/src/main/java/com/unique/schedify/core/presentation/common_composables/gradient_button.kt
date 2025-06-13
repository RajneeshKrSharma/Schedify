package com.unique.schedify.core.presentation.common_composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import com.google.firebase.annotations.concurrent.Background
import com.unique.schedify.core.presentation.utils.size_units.dp1
import com.unique.schedify.core.presentation.utils.size_units.dp12
import com.unique.schedify.core.presentation.utils.size_units.dp24
import com.unique.schedify.core.presentation.utils.size_units.dp8

@Composable
fun GradientButton(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium.copy(
        color = MaterialTheme.colorScheme.onSecondaryContainer
    ),
    btnGradient: Brush? = null,
    enabled: Boolean = true,
    iconModifier: Modifier = Modifier,
    icon: Int? = null,
    onClick: () -> Unit
) {

    val gradient = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onSecondary
        )
    )

    Box(
        modifier = modifier
            .background(
                shape = RoundedCornerShape(dp12),
                brush = if (enabled) btnGradient
                    ?: gradient else SolidColor(MaterialTheme.colorScheme.onTertiary)
            )
            .clip(RoundedCornerShape(dp12))
            .clickable(enabled = enabled) { onClick() },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = dp24)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                icon?.let {
                    Image(
                        modifier = iconModifier,
                        painter = painterResource(icon),
                        contentDescription = null
                    )
                    Spacer(Modifier.width(dp8))
                }
                Text(
                    text = text,
                    style = textStyle
                )
            }
        }
    }
}


@Composable
fun OutlinedGradientButton(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium.copy(
        color = MaterialTheme.colorScheme.primary
    ),
    background: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    borderGradient: Brush? = null,
    enabled: Boolean = true,
    iconModifier: Modifier = Modifier,
    icon: Int? = null,
    onClick: () -> Unit
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onSecondary
        )
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(dp12))
            .background(
                color = background,
                shape = RoundedCornerShape(dp12)
            )
            .border(
                width = dp1,
                brush = if (enabled) borderGradient ?: gradient
                else SolidColor(MaterialTheme.colorScheme.onTertiary),
                shape = RoundedCornerShape(dp12)
            )
            .clickable(enabled = enabled) { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = dp24)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                icon?.let {
                    Image(
                        modifier = iconModifier,
                        painter = painterResource(icon),
                        contentDescription = null
                    )
                    Spacer(Modifier.width(dp8))
                }
                Text(
                    text = text,
                    style = textStyle
                )
            }
        }
    }
}

