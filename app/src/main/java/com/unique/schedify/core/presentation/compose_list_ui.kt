package com.unique.schedify.core.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.core.presentation.base_composables.BaseCompose
import com.unique.schedify.core.presentation.common_composables.CellUiDetails
import com.unique.schedify.core.presentation.common_composables.InfiniteAutoSlidingCarousel
import com.unique.schedify.core.presentation.common_composables.PageFallAnimatedList
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.ui_utils.AppBaseGradients
import com.unique.schedify.post_auth.split_expense.presentation.utils.EmptyDataUi

abstract class ListUi {
    abstract fun listOfCellDetails(): List<CellUiDetails>
    open fun listOfCarouselData(): List<CellUiDetails>? = null

    @Composable
    fun ComposeListUi(
        navController: NavController,
        topAppBar: @Composable (() -> Unit)?,
        modifier: Modifier = Modifier,
        content: @Composable (() -> Unit)? = null,
        listUiTitle: String,
    ) {
        BaseCompose(
            topBar = topAppBar,
            modifier = modifier,
            navController = navController,
            brush = AppBaseGradients.baseBgDarkGradient()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                listOfCarouselData()?.let { carouselData ->
                    if (carouselData.isNotEmpty()) {
                        Text(
                            modifier = Modifier
                                .padding(start = dp16, top = dp16),
                            text = stringResource(R.string.the_faves_picks_for_you),
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        )

                        Spacer(modifier = Modifier.height(dp16))

                        InfiniteAutoSlidingCarousel(
                            modifier = Modifier.weight(0.50f),
                            carouselData
                        )
                    }
                }

                ListUiComposable(
                    modifier = Modifier.weight(0.50f),
                    title = listUiTitle,
                    navController = navController
                )
            }
            content?.invoke()
        }
    }

    @Composable
    private fun ListUiComposable(
        modifier: Modifier,
        title: String,
        navController: NavController
    ) {
        Column(
            modifier = modifier
                .padding(dp16)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )

            Spacer(modifier = Modifier.height(dp8))

            if (listOfCellDetails().isNotEmpty())
                PageFallAnimatedList(
                    listOfScreens = listOfCellDetails()
                ) { selectedScreenOption ->
                    Navigation.navigateToScreen(
                        navigateTo = selectedScreenOption,
                        navController = navController
                    )
                }
            else {
                EmptyDataUi(
                    imageUrl = "",
                    msg = stringResource(R.string.listofcelldetails_is_null),
                )
            }
        }
    }
}
