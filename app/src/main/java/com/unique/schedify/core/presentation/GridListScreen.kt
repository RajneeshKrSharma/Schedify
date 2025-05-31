package com.unique.schedify.core.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.core.presentation.common_composables.GridCellDetails
import com.unique.schedify.core.presentation.base_composables.BaseCompose
import com.unique.schedify.core.presentation.common_composables.PageFallAnimatedGrid
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp8

abstract class GridListScreen {
    abstract fun listOfGridCellDetails(): MutableList<GridCellDetails>
    open fun content(): @Composable (() -> Unit)? = null

    @Composable
    fun ComposeGridListScreen(
        navController: NavController,
        topAppBar: @Composable (() -> Unit)?,
        modifier: Modifier = Modifier,
        columnModifier: Modifier = Modifier,
        boxModifier: Modifier = Modifier
    ) {
        BaseCompose(
            topBar = topAppBar,
            modifier = modifier,
            navController = navController
        ) {
            Column(
                modifier = columnModifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = boxModifier
                        .weight(1f)
                        .padding(horizontal = dp16, vertical = dp8)
                        .background(color = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    PageFallAnimatedGrid(
                        listOfScreens = listOfGridCellDetails()
                    ) { selectedScreenOption ->
                        Navigation.navigateToScreen(
                            navigateTo = selectedScreenOption, navController = navController
                        )
                    }
                }
                content()?.invoke()
            }
        }
    }
}
