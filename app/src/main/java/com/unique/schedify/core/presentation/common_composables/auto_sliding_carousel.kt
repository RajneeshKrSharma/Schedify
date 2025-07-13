package com.unique.schedify.core.presentation.common_composables

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.util.lerp
import com.unique.schedify.R
import com.unique.schedify.core.presentation.utils.size_units.dp100
import com.unique.schedify.core.presentation.utils.size_units.dp150
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp20
import com.unique.schedify.core.presentation.utils.size_units.dp64
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.size_units.sp16
import com.unique.schedify.core.presentation.utils.size_units.sp20
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@Composable
fun InfiniteAutoSlidingCarousel(
    modifier: Modifier,
    items: List<CellUiDetails>
) {
    val realItemCount = items.size
    val virtualPageCount = Int.MAX_VALUE
    val initialPage = virtualPageCount / 2

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { virtualPageCount }
    )

    LaunchedEffect(Unit) {
        pagerState.scrollToPage(initialPage)
        while (true) {
            delay(1000)
            val nextPage = pagerState.currentPage + 1
            pagerState.animateScrollToPage(
                nextPage,
                animationSpec = tween(durationMillis = 1000)
            )
        }
    }

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        userScrollEnabled = false,
        contentPadding = PaddingValues(horizontal = dp64),
        pageSpacing = dp8,
    ) { page ->

        val currentPageOffset = (
                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                ).absoluteValue

        val scale = lerp(0.85f, 1.2f, 1f - currentPageOffset.coerceIn(0f, 1f))
        val yOffset = lerp(-30f, 0f, 1f - currentPageOffset.coerceIn(0f, 1f))

        val actualIndex = page % realItemCount

        Card(
            modifier = Modifier
                .padding(start = dp8, end = dp8)
                .fillMaxWidth()
                .aspectRatio(1f)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationY = yOffset // shift smaller items up
                },
            shape = RoundedCornerShape(dp16),
            elevation = CardDefaults.cardElevation(dp8),
            colors = CardDefaults.cardColors(containerColor =
                MaterialTheme.colorScheme.onSecondaryContainer)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.verticalGradient(
                            items[actualIndex].backgroundCardGradientColors ?:
                            listOf(
                                MaterialTheme.colorScheme.onSecondaryContainer,
                                MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        items[actualIndex].imageUrl?.let { url ->
                            ImageWithLoadingIndicator(
                                imageUrl = url,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.8f),
                                imageModifier = Modifier.fillMaxSize()
                            )
                        } ?: Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.8f),
                            painter = painterResource(R.drawable.schedify),
                            contentDescription = ""
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RectangleShape)
                                .background(color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    shape = RoundedCornerShape(topStart = dp150, topEnd = dp150)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(vertical = dp8),
                                text = items[actualIndex].text,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = sp16,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}