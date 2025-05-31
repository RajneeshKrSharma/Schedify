package com.unique.schedify.post_auth.home.presentation

import com.unique.schedify.core.presentation.navigation.Navigation
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.core.presentation.common_composables.GridCellDetails
import com.unique.schedify.core.presentation.GridListScreen
import com.unique.schedify.core.presentation.common_composables.ActionIcons
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import com.unique.schedify.post_auth.post_auth_utils.HomeGridCellType


class HomeScreenClass : GridListScreen() {
    private val listOfGridCellDetails = mutableListOf<GridCellDetails>().apply {
        add(
            GridCellDetails(
                screen = AvailableScreens.PostAuth.ScheduleListScreen,
                image = R.drawable.schedule_list_icon,
                text = HomeGridCellType.SIMPLE.description
            )
        )
        add(
            GridCellDetails(
                screen = AvailableScreens.PostAuth.SplitScheduleListScreen,
                image = R.drawable.split_expense_icon,
                text = HomeGridCellType.SPLIT.description
            )
        )
    }

    override fun listOfGridCellDetails(): MutableList<GridCellDetails> = listOfGridCellDetails
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewmodel: HomeViewmodel = hiltViewModel(),
    navController: NavController,
) {
    HomeScreenClass().ComposeGridListScreen(
        navController = navController,
        topAppBar = {
            TopAppBar(
                title = { Text(
                    text = stringResource(R.string.select_to_proceed),
                    style = MaterialTheme.typography.headlineSmall
                ) },
                modifier = Modifier.padding(dp16),
                actions = {
                    ActionIcons(
                        iconText = stringResource(R.string.logout),
                        borderStrokeColor = MaterialTheme.colorScheme.secondary
                    ) {
                        viewmodel.logout()
                        Navigation.navigateAndClearBackStackScreen(
                            navigateTo = AvailableScreens.PreAuth.LoginScreen,
                            navController = navController
                        )
                    }
                }
            )
        }
    )
}