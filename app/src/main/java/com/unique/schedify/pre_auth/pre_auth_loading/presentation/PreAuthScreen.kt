package com.unique.schedify.pre_auth.pre_auth_loading.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.core.presentation.common_composables.GridCellDetails
import com.unique.schedify.core.presentation.GridListScreen
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import com.unique.schedify.pre_auth.pre_auth_utils.PreAuthGridCellType


class PreAuthScreenClass(
    private val gridCellProvider: MutableList<GridCellDetails>
) : GridListScreen() {
    override fun listOfGridCellDetails(): MutableList<GridCellDetails> = gridCellProvider
}

@Composable
fun PreAuthScreen(
    navController: NavController
) {
    PreAuthScreenClass(getListOfGridCellsDetails()).ComposeGridListScreen(
        navController = navController,
        topAppBar = { TopPreAuthScreen() }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopPreAuthScreen() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.select_to_proceed),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        modifier = Modifier.padding(dp16),
    )
}

fun getListOfGridCellsDetails() = mutableListOf<GridCellDetails>().apply {
    add(
        GridCellDetails(
            screen = AvailableScreens.PreAuth.LoginScreen,
            image = R.drawable.login_icon,
            text = PreAuthGridCellType.Login.description
        )
    )
}
