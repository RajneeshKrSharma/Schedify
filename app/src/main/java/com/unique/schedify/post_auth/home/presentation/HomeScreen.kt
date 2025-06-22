package com.unique.schedify.post_auth.home.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.core.presentation.ListUi
import com.unique.schedify.core.presentation.common_composables.ActionIcons
import com.unique.schedify.core.presentation.common_composables.CellUiDetails
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.size_units.sp18
import com.unique.schedify.core.presentation.utils.size_units.sp20
import com.unique.schedify.core.presentation.utils.size_units.sp24
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens


class HomeScreenClass(
    private val listOfCellDetails: List<CellUiDetails>,
    private val listOfCarouselData: List<CellUiDetails>,
) : ListUi() {
    override fun listOfCellDetails(): List<CellUiDetails> = listOfCellDetails
    override fun listOfCarouselData(): List<CellUiDetails> = listOfCarouselData
}

@Composable
fun HomeScreen(
    viewmodel: HomeViewmodel = hiltViewModel(),
    navController: NavController,
) {

    val homeListData by viewmodel.homeListCellDetails.collectAsStateWithLifecycle()
    val homeCarouselListCellDetails by viewmodel.homeCarouselListCellDetails.collectAsStateWithLifecycle()

    HomeScreenClass(
        listOfCellDetails = homeListData,
        listOfCarouselData = homeCarouselListCellDetails,
    ).ComposeListUi(
        navController = navController,
        topAppBar = {
            HomeScreenAppBar(
                userName = viewmodel.getUserName(),
                onLogoutClicked = {
                    viewmodel.logout()
                    Navigation.navigateAndClearBackStackScreen(
                        navigateTo = AvailableScreens.PreAuth.LoginScreen,
                        navController = navController
                    )
                },
                onWorkerTrackIconClicked = {
                    Navigation.navigateToScreen(
                        navigateTo = AvailableScreens.PostAuth.UserMappedWeatherScreen,
                        navController = navController
                    )
                }
            )
        },
        listUiTitle = stringResource(R.string.let_s_list_something)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenAppBar(
    userName: String,
    onLogoutClicked: () -> Unit,
    onWorkerTrackIconClicked: () -> Unit,
) {
    TopAppBar(
        title = { Text(
            text = stringResource(R.string.hi, userName),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = sp20,
                color = MaterialTheme.colorScheme.primary,
            )
        ) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.secondary
        ),
        modifier = Modifier
            .padding(dp8),
        actions = {
//            ActionIcons(
//                iconText = "Track W.S",
//                borderStrokeColor = MaterialTheme.colorScheme.onPrimaryContainer
//            ) {
//                onWorkerTrackIconClicked()
//            }
            ActionIcons(
                iconText = stringResource(R.string.logout),
                borderStrokeColor = MaterialTheme.colorScheme.secondary
            ) {
                onLogoutClicked()
            }
        }
    )
}