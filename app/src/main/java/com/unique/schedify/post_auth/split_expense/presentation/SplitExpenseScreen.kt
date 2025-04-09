package com.unique.schedify.post_auth.split_expense.presentation


import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.navigation.NavController
import com.google.gson.Gson
import com.unique.schedify.R
import com.unique.schedify.core.presentation.base_composables.BaseCompose
import com.unique.schedify.core.presentation.common_composables.ActionIcons
import com.unique.schedify.core.presentation.common_composables.EditableTextDialog
import com.unique.schedify.core.presentation.common_composables.LoadingUi
import com.unique.schedify.core.presentation.common_composables.StaticChips
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp1
import com.unique.schedify.core.presentation.utils.size_units.dp10
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp24
import com.unique.schedify.core.presentation.utils.size_units.dp4
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import com.unique.schedify.core.util.Resource
import com.unique.schedify.post_auth.post_auth_utils.GroupState
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitListScreen(
    appBarText: String,
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
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onTertiary),
        ) {
            content()
        }
    }
}

@Composable
fun GroupListScreen(
    navController: NavController,
    splitExpenseViewModel: SplitExpenseViewModel
) {
    val context = LocalContext.current
    SplitListScreen(
        appBarText = stringResource(R.string.groups),
        appBarActions = {
            GroupTopBarActions(splitExpenseViewModel = splitExpenseViewModel)
        }
    ) {
        when (val state = splitExpenseViewModel.getAllGroupDetails.value) {
            is Resource.Default -> {}
            is Resource.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }

            is Resource.Loading -> {
                LoadingUi()
            }

            is Resource.Success -> {
                val listOfGroups = state.data?.data ?: listOf()
                Box(
                    modifier = Modifier.padding(dp10)
                ) {
                    LazyColumn {
                        items(listOfGroups.size) { index ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(horizontal = dp4, vertical = dp4)
                                    .background(
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        shape = RoundedCornerShape(dp8)
                                    )
                                    .clickable {
                                        val groupJson = Gson().toJson(listOfGroups[index])
                                        val encodedGroupJson = URLEncoder.encode(
                                            groupJson,
                                            StandardCharsets.UTF_8.toString()
                                        )

                                        Navigation.navigateToScreen(
                                            navigateTo = AvailableScreens.PostAuth.AddCollaboratorScreen,
                                            navController = navController,
                                        )
                                    }

                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(dp16)
                                ) {
                                    Text(
                                        listOfGroups[index]?.grpName
                                            ?: stringResource(R.string.not_applicable),
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                    Row(
                                        modifier = Modifier
                                            .padding(vertical = dp8)
                                    ) {
                                        StaticChips(
                                            text = buildAnnotatedString {
                                                append(stringResource(R.string.collaborators))
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("${listOfGroups[index]?.getTotalCollaborator()}")
                                                }
                                            },

                                            color = MaterialTheme.colorScheme.tertiary,
                                        )
                                        Spacer(modifier = Modifier.width(dp8))
                                        StaticChips(
                                            text = buildAnnotatedString {
                                                append("Expenses : ")
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("${listOfGroups[index]?.getTotalExpensesCount()}")
                                                }
                                            },
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    with(splitExpenseViewModel) {
                        if (groupState.value is Resource.Loading ||
                            collaboratorState.value is Resource.Loading
                        ) {
                            LoadingUi()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupTopBarActions(
    splitExpenseViewModel: SplitExpenseViewModel
) {
    var showAddOptionDialog by remember { mutableStateOf(false) }
    ActionIcons(
        modifier = Modifier
            .clip(RoundedCornerShape(dp24))
            .background(color = MaterialTheme.colorScheme.primary)
            .border(
                BorderStroke(dp1, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(dp24)
            ),

        iconText = stringResource(R.string.add_group),
        textColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        showAddOptionDialog = true
    }
    if (showAddOptionDialog) {
        EditableTextDialog(
            editFieldText = stringResource(R.string.enter_group_name),
            alertDialogTitle = stringResource(R.string.add_group),
            onConfirm = { groupName ->
                showAddOptionDialog = false
                splitExpenseViewModel.startGroupChosenProcess(
                    GroupState.CREATE,
                    groupName
                )
            }
        )
    }
}
