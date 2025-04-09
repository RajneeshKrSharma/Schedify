package com.unique.schedify.post_auth.split_expense.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.navigation.NavController
import com.google.gson.Gson
import com.unique.schedify.R
import com.unique.schedify.core.presentation.common_composables.ActionIcons
import com.unique.schedify.core.presentation.common_composables.DashedDivider
import com.unique.schedify.core.presentation.common_composables.EditableTextDialog
import com.unique.schedify.core.presentation.common_composables.MultiOptionRadioDialog
import com.unique.schedify.core.presentation.common_composables.StaticChips
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp10
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp24
import com.unique.schedify.core.presentation.utils.size_units.dp6
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import com.unique.schedify.core.util.date_utils.formattedServerDateTime
import com.unique.schedify.post_auth.post_auth_utils.CollaboratorState
import com.unique.schedify.post_auth.post_auth_utils.GroupViewMode
import com.unique.schedify.post_auth.post_auth_utils.SplitScheduleListMoreOption
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun LoadCollaboratorScreen(
    navController: NavController,
    grpItem: GroupExpenseResponseDto.Data,
    splitExpenseViewModel: SplitExpenseViewModel
) {

    val grpDetails by splitExpenseViewModel.getAllGroupDetails

    val groupItem by remember(grpDetails) {
        derivedStateOf {
            grpDetails.data?.data?.find { it?.id == grpItem.id } ?: grpItem
        }
    }

    var showCollaboratorAddDialog by remember { mutableStateOf(false) }

    SplitListScreen(
        appBarText = "Group Info",
        appBarActions = {
            CollaboratorTopBarActions(
                splitExpenseViewModel = splitExpenseViewModel,
                groupItem = groupItem
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.onTertiary,
                    shape = RoundedCornerShape(dp8)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(dp10)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            shape = RoundedCornerShape(dp8)
                        )

                ) {
                    Column(
                        modifier = Modifier
                            .padding(dp16)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start

                        ) {
                            Text(
                                stringResource(
                                    R.string.group,
                                    groupItem.grpName ?: stringResource(R.string.not_applicable)
                                ),
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
                                            append("${groupItem.getTotalCollaborator()}")
                                        }
                                    },
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Spacer(modifier = Modifier.width(dp8))
                                StaticChips(
                                    text = buildAnnotatedString {
                                        append(stringResource(R.string.expenses))
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("${groupItem.getTotalExpensesCount()}")
                                        }
                                    },
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        DashedDivider()

                        when (splitExpenseViewModel.groupViewModeState.value) {
                            GroupViewMode.COLLABORATOR_LIST -> {
                                groupItem.collaborators?.let { allCollaborator ->
                                    LazyColumn(
                                        contentPadding = PaddingValues(vertical = dp16),
                                        verticalArrangement = Arrangement.spacedBy(dp8)
                                    ) {
                                        items(allCollaborator.size) { index ->
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .wrapContentHeight()
                                                    .background(
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                        shape = RoundedCornerShape(dp8)
                                                    )
                                                    .clickable {
                                                        val groupJson =
                                                            Gson().toJson(allCollaborator[index])
                                                        val encodedGroupJson = URLEncoder.encode(
                                                            groupJson,
                                                            StandardCharsets.UTF_8.toString()
                                                        )

                                                        Navigation.navigateToScreenWithArgs(
                                                            navigateTo = AvailableScreens.PostAuth.SplitScheduleListDetailScreen,
                                                            navController = navController,
                                                            args = encodedGroupJson
                                                        )
                                                    },
                                                shape = RoundedCornerShape(dp8),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer
                                                ),
                                                elevation = CardDefaults.cardElevation(
                                                    defaultElevation = dp6
                                                ),
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(dp16),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                ) {
                                                    Text(
                                                        allCollaborator[index]?.collaboratorName
                                                            ?: stringResource(R.string.not_applicable),
                                                        style = MaterialTheme.typography.headlineSmall.copy(
                                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                                        )
                                                    )
                                                    StaticChips(
                                                        text = buildAnnotatedString {
                                                            append("Expenses : ")
                                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                                append("${allCollaborator[index]?.expenses?.size ?: 0}")
                                                            }
                                                        },
                                                        color = MaterialTheme.colorScheme.onPrimary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            GroupViewMode.EXPENSE_LIST -> {
                                val allExpenses: List<GroupExpenseResponseDto.Data.Collaborator.Expense> =
                                    groupItem.collaborators
                                        ?.filterNotNull()
                                        ?.flatMap { it.expenses.orEmpty().filterNotNull() }
                                        ?: emptyList()

                                LazyColumn(
                                    contentPadding = PaddingValues(vertical = dp16),
                                    verticalArrangement = Arrangement.spacedBy(dp8)
                                ) {
                                    items(allExpenses.size) { index ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .wrapContentHeight()
                                                .background(
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    shape = RoundedCornerShape(dp8)
                                                ),
                                            shape = RoundedCornerShape(dp8),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.onSecondaryContainer
                                            ),
                                            elevation = CardDefaults.cardElevation(
                                                defaultElevation = dp6
                                            ),
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(dp16)
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                    ) {
                                                        Text(
                                                            allExpenses[index].iName
                                                                ?: stringResource(R.string.not_applicable),
                                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                                            )
                                                        )

                                                        StaticChips(
                                                            text = buildAnnotatedString {
                                                                append("₹ ${allExpenses[index].iAmt}")
                                                            },
                                                            textStyle = MaterialTheme.typography.titleMedium.copy(
                                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                                            ),
                                                            color = MaterialTheme.colorScheme.secondary
                                                        )
                                                    }

                                                    Text(
                                                        allExpenses[index].iDesp
                                                            ?: stringResource(R.string.not_applicable),
                                                        style = MaterialTheme.typography.titleMedium.copy(
                                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                                        )
                                                    )

                                                    DashedDivider(
                                                        vhSpace = dp8
                                                    )

                                                    Column {
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(dp8),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                        ) {
                                                            Text(
                                                                "\uD83D\uDCE6 Qty: ${
                                                                    allExpenses[index].iQty ?: stringResource(
                                                                        R.string.not_applicable
                                                                    )
                                                                }",
                                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                                )
                                                            )

                                                            Text(
                                                                formattedServerDateTime(allExpenses[index].dateTime),

                                                                )
                                                        }

                                                        StaticChips(
                                                            text = buildAnnotatedString {
                                                                append(allExpenses[index].iNotes)
                                                            },
                                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                                            ),
                                                            color = MaterialTheme.colorScheme.onTertiary
                                                        )
                                                    }

                                                }

                                                if (allExpenses[index].isSettled == true)
                                                    Text(
                                                        modifier = Modifier
                                                            .graphicsLayer(
                                                                rotationZ = -10f
                                                            ),
                                                        text = "SETTLED",
                                                        style = MaterialTheme.typography.displayMedium.copy(
                                                            color = MaterialTheme.colorScheme.onSecondary.copy(
                                                                alpha = 0.2f
                                                            )
                                                        )
                                                    )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        when (splitExpenseViewModel.splitScheduleListMoreOption.value) {
                            SplitScheduleListMoreOption.ADD_COLLABORATOR -> {
                                /*Navigation.navigateToScreen(
                                    navigateTo = AvailableScreens.PostAuth.AddCollaboratorScreen, navController = navController
                                )*/
                                /*if (showCollaboratorAddDialog) {
                                    groupItem.id?.let { grpId ->
                                        EditableTextDialog(
                                            editFieldText = stringResource(R.string.enter_collaborator_name),
                                            alertDialogTitle = stringResource(R.string.add_collaborator),
                                            onConfirm = { name ->
                                                showCollaboratorAddDialog = false
                                                splitExpenseViewModel.startCollaboratorChosenProcess(
                                                    CollaboratorState.CREATE,
                                                    collaboratorName = name,
                                                    groupId = grpId
                                                )
                                            }
                                        )
                                    }
                                }*/
                            }
                            SplitScheduleListMoreOption.ADD_EXPENSES -> {

                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun CollaboratorTopBarActions(
    splitExpenseViewModel: SplitExpenseViewModel,
    groupItem: GroupExpenseResponseDto.Data
) {
    var showViewModeOptionDialog by remember { mutableStateOf(false) }
    var showMoreOptionDialog by remember { mutableStateOf(false) }

    Row {
        ActionIcons(
            modifier = Modifier
                .clip(RoundedCornerShape(dp24))
                .background(
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.85f
                    )
                ),

            iconText = stringResource(R.string.view_mode),
            textColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            showViewModeOptionDialog = true
        }

        Spacer(modifier = Modifier.width(dp8))

        ActionIcons(
            modifier = Modifier
                .clip(RoundedCornerShape(dp24))
                .background(
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.85f
                    )
                ),
            icon = Icons.Default.MoreVert,
            iconText = stringResource(R.string.more),
            textColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            showMoreOptionDialog = true
        }
    }

    if(showViewModeOptionDialog) {
        MultiOptionRadioDialog(
            alertDialogTitle = "Group View Mode",
            options = listOf(
                GroupViewMode.COLLABORATOR_LIST.description,
                GroupViewMode.EXPENSE_LIST.description),
            selectedOption = splitExpenseViewModel.groupViewModeState.value.description,
            onConfirm = { selectedValue ->
                showViewModeOptionDialog = false
                splitExpenseViewModel.groupViewModeFilter(
                    GroupViewMode.fromDescription(selectedValue)
                )
            },
        )
    }

    if(showMoreOptionDialog) {
        MultiOptionRadioDialog(
            alertDialogTitle = "Addons",
            options = listOf(
                SplitScheduleListMoreOption.ADD_COLLABORATOR.description,
                SplitScheduleListMoreOption.ADD_EXPENSES.description),
            selectedOption = splitExpenseViewModel.splitScheduleListMoreOption.value.description,
            onConfirm = { selectedValue ->
                showMoreOptionDialog = false
                splitExpenseViewModel.splitScheduleListMoreOptionFilter(
                    SplitScheduleListMoreOption.fromDescription(selectedValue)
                )
            },
        )
    }
}