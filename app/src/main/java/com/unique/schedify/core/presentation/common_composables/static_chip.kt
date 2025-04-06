package com.unique.schedify.core.presentation.common_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import com.unique.schedify.core.presentation.utils.size_units.dp8

@Composable
fun StaticChips(
    text: AnnotatedString,
    color: Color,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall.copy(
        color= MaterialTheme.colorScheme.onSecondaryContainer
    )
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .background(
                color = color,
                shape = RoundedCornerShape(dp8)
            )
    ) {
        Text(
            modifier = Modifier.padding(dp8),
            text = text,
            style = textStyle
        )
    }
}