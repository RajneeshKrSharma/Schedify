package com.unique.schedify.post_auth.split_expense.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.unique.schedify.core.presentation.base_composables.BaseCompose
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import com.unique.schedify.post_auth.post_auth_utils.SplitScheduleListMoreOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitListScreen(
    appBarText: String,
    navController: NavController,
    appBarActions: @Composable (RowScope.() -> Unit)? = null,
    content: @Composable (() -> Unit)
) {
    BaseCompose(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = appBarText,
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                modifier = Modifier.padding(dp16),
                actions = { appBarActions?.invoke(this) }
            )
        },
        navController = navController
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onTertiaryContainer),
        ) {
            content()
        }
    }
}

fun performNavigation(
    navController: NavController,
    performActionFor: Any,
    args: Any
) {
    when(performActionFor) {
        SplitScheduleListMoreOption.COLLABORATOR_SCREEN -> {
            Navigation.navigateToScreenWithArgs(
                navigateTo = AvailableScreens.PostAuth.SplitScheduleListDetailScreen,
                navController = navController,
                args = args
            )
        }
        SplitScheduleListMoreOption.EXPENSES_SCREEN -> {
            Navigation.navigateToScreenWithArgs(
                navigateTo = AvailableScreens.PostAuth.ExpenseScreen,
                navController = navController,
                args = args
            )
        }
    }
}
