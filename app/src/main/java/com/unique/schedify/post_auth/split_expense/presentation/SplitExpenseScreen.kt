package com.unique.schedify.post_auth.split_expense.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.core.presentation.base_composables.BaseCompose
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp1
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp4
import com.unique.schedify.core.presentation.utils.size_units.dp8
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
                navigationIcon = {
                    IconButton(
                        onClick = {
                                Navigation.navigateBack(navController = navController)
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                title = {
                    Text(
                        text = appBarText,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.secondary
                ),
                actions = { appBarActions?.invoke(this) }
            )
        },
        navController = navController
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onSecondaryContainer),
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

@Composable
fun ExpenseTagBox(
    label: String,
    amount: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(dp8))
            .background(color = MaterialTheme.colorScheme.onSecondaryContainer)
            .border(
                BorderStroke(
                    dp1,
                    MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(dp8)
            )
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .padding(dp4)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(dp4),
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.7f)
                ),
            )

            Text(
                modifier = Modifier.padding(dp4),
                text = stringResource(R.string.rupee_symbol, amount),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.surfaceBright
                ),
            )
        }
    }
}

