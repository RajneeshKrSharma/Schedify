package com.unique.schedify.core.presentation.common_composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.unique.schedify.core.presentation.utils.size_units.dp12
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp28
import com.unique.schedify.core.presentation.utils.size_units.dp4
import com.unique.schedify.core.presentation.utils.size_units.dp40
import com.unique.schedify.core.presentation.utils.size_units.dp56
import com.unique.schedify.core.presentation.utils.size_units.dp8

@Composable
fun CustomSearchBar(
    modifier: Modifier = Modifier,
    hint: String = "Search...",
    onSearch: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    var isExpanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    val transition = updateTransition(targetState = isExpanded, label = "search-bar-expand")

    val width by transition.animateDp(
        transitionSpec = {
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        },
        label = "width"
    ) { expanded -> if (expanded) screenWidth else dp40 }

    val cornerRadius by transition.animateDp(
        transitionSpec = {
            spring(dampingRatio = Spring.DampingRatioLowBouncy)
        },
        label = "corner"
    ) { if (it) dp16 else dp28 }

    Surface(
        modifier = modifier
            .height(dp56)
            .width(width)
            .clip(RoundedCornerShape(cornerRadius))
            .shadow(dp4, RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.onSecondaryContainer)
            .clickable { isExpanded = true },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = dp4,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dp12),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(tween(300)) + expandHorizontally(),
                exit = fadeOut(tween(200)) + shrinkHorizontally()
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(dp8))

                    TextField(
                        value = query,
                        onValueChange = {
                            query = it
                            onSearch(it)
                        },
                        placeholder = {
                            Text(text = hint, color = MaterialTheme.colorScheme.primary)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surfaceContainerLow),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            focusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    )

                    AnimatedVisibility(
                        visible = query.isNotEmpty(),
                        enter = fadeIn(tween(200)) + scaleIn(),
                        exit = fadeOut(tween(150)) + scaleOut()
                    ) {
                        IconButton(onClick = {
                            query = ""
                            onSearch("")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

