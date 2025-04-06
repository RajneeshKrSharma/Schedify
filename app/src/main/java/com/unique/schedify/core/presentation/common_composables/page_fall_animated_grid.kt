package com.unique.schedify.core.presentation.common_composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unique.schedify.core.presentation.utils.size_units.dp10
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.size_units.sp50
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import kotlinx.coroutines.delay

@Composable
fun PageFallAnimatedGrid(
    modifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    listOfScreens: List<GridCellDetails>,
    onClick: (AvailableScreens) -> Unit,
) {
    val visibleItems = remember {
        mutableStateListOf<Boolean>().apply {
            repeat(listOfScreens.size) { add(false) }
        }
    }

    LaunchedEffect(Unit) {
        for (index in listOfScreens.indices) {
            delay(200)
            visibleItems[index] = true
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(dp8),
        verticalArrangement = Arrangement.spacedBy(dp8)
    ) {
        itemsIndexed(listOfScreens) { index, item ->
            AnimatedVisibility(
                visible = visibleItems[index],
                enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                )
            ) {
                Card(
                    modifier = itemModifier
                        .clip(RoundedCornerShape(dp16))
                        .clickable { onClick(item.screen) },
                    elevation = CardDefaults.cardElevation(dp8)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(dp16))
                            .background(Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color.Blue, Color.Black)
                                    ),
                                    shape = RoundedCornerShape(dp16)
                                )
                                .clip(RoundedCornerShape(dp16))
                        ) {
                            Column(
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Image(
                                    modifier = imageModifier.padding(dp8),
                                    painter = painterResource(item.image),
                                    contentDescription = ""
                                )

                                Text(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    text = item.text,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                )

                                Spacer(Modifier.height(dp10))
                            }
                        }

                        Text(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .offset(x = dp10, y = (-20).dp),
                            text = (index + 1).toString(),
                            fontSize = sp50,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    offset = Offset(7f, 7f),
                                    blurRadius = 10f
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}