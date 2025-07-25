package com.unique.schedify.post_auth.split_expense.presentation

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.core.presentation.common_composables.ActionIcons
import com.unique.schedify.core.presentation.common_composables.CustomChip
import com.unique.schedify.core.presentation.common_composables.CustomDialog
import com.unique.schedify.core.presentation.common_composables.DashedDivider
import com.unique.schedify.core.presentation.common_composables.LoadingUi
import com.unique.schedify.core.presentation.common_composables.StaticChips
import com.unique.schedify.core.presentation.common_composables.VerticalDashedDivider
import com.unique.schedify.core.presentation.utils.size_units.dp1
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp2
import com.unique.schedify.core.presentation.utils.size_units.dp24
import com.unique.schedify.core.presentation.utils.size_units.dp4
import com.unique.schedify.core.presentation.utils.size_units.dp6
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.size_units.sp24
import com.unique.schedify.core.presentation.utils.ui_utils.showToast
import com.unique.schedify.core.util.Resource
import com.unique.schedify.core.util.date_utils.formattedServerDateTime
import com.unique.schedify.post_auth.post_auth_utils.ExpenseAlterState
import com.unique.schedify.post_auth.post_auth_utils.ExpenseState
import com.unique.schedify.post_auth.post_auth_utils.ExpenseType
import com.unique.schedify.post_auth.split_expense.data.remote.dto.ExpenseRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.ExpenseUpdateDeleteRequestPostData
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import com.unique.schedify.post_auth.split_expense.presentation.utils.EmptyDataUi
import com.unique.schedify.post_auth.split_expense.presentation.utils.ExpenseNomenclature
import com.unique.schedify.post_auth.split_expense.presentation.utils.GenericBottomSheet
import com.unique.schedify.post_auth.split_expense.presentation.utils.buildAddExpenseFormFields
import com.unique.schedify.post_auth.split_expense.presentation.utils.buildEditExpenseFormFields
import com.unique.schedify.post_auth.split_expense.presentation.utils.getColorForExpenseNomenclature
import com.unique.schedify.post_auth.split_expense.presentation.utils.getColorForExpenseType
import com.unique.schedify.post_auth.split_expense.presentation.utils.prepareExpenseRequest
import kotlinx.coroutines.launch

@Composable
fun ExpenseScreen(
    navController: NavController,
    collaborator: GroupExpenseResponseDto.Collaborator,
    splitExpenseViewModel: SplitExpenseViewModel
) {

    val context = LocalContext.current
    val collaboratorItem by remember {
        derivedStateOf {
            splitExpenseViewModel.getAllGroupDetails.value.data
                ?.firstNotNullOfOrNull { group ->
                    group.collaborators?.firstOrNull { it?.id == collaborator.id }
                }
        }
    }

    val groupItem by remember {
        derivedStateOf {
            splitExpenseViewModel.getAllGroupDetails.value.data
                ?.firstOrNull { it.id == collaboratorItem?.groupId }
        }
    }

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { newValue ->
            newValue != ModalBottomSheetValue.Hidden
        }
    )

    val coroutine = rememberCoroutineScope()

    val updateDeleteData = splitExpenseViewModel.updateDeleteInfoState.value

    val editExpenseBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { newValue ->
            newValue != ModalBottomSheetValue.Hidden
        }
    )

    SplitListScreen(
        appBarText = stringResource(R.string.expenses),
        navController = navController,
        appBarActions = {
            collaboratorItem?.let { clbItem ->
                if (clbItem.collabUserId == splitExpenseViewModel.getAuthUserId() &&
                    clbItem.getTotalExpense() > 0
                ) {
                    ExpenseTopBarActions(
                        onButtonClick = {
                            splitExpenseViewModel.performExpenseAlteringActions(
                                perform = ExpenseState.CREATE
                            )
                        }
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dp16),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            collaboratorItem?.let { clbItem ->
                groupItem?.let { grpItem ->
                    clbItem to grpItem
                }
            }?.let { (clbItem, grpItem) ->

                Column {
                    Row {
                        CustomChip(
                            text = "${grpItem.name}",
                            textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            bgColor = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )

                        Spacer(modifier = Modifier.padding(horizontal = dp4))

                        CustomChip(
                            text = clbItem.collabName(),
                            textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            bgColor = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(dp16))

                    TotalExpenseDetailsUi(
                        grpItem = grpItem,
                        clbItem = clbItem
                    )

                    Spacer(modifier = Modifier.height(dp16))

                    if (clbItem.collabUserId == splitExpenseViewModel.getAuthUserId()
                        && clbItem.getTotalExpense() == 0
                    ) {
                        EmptyDataUi(
                            imageUrl = "https://schedify.pythonanywhere.com/media/pictures/add_expense.png",
                            msg = "Add Your First Expense !",
                            content = {
                                AddExpenseButton(
                                    onButtonClick = {
                                        splitExpenseViewModel.performExpenseAlteringActions(
                                            perform = ExpenseState.CREATE
                                        )
                                    }
                                )
                            }
                        )

                    } else if (clbItem.collabUserId != splitExpenseViewModel.getAuthUserId() &&
                        clbItem.getTotalExpense() == 0
                    ) {
                        EmptyDataUi(
                            imageUrl = "https://schedify.pythonanywhere.com/media/pictures/add_expense_normal.png",
                            msg = stringResource(R.string.no_expense_available),
                        )
                    } else {
                        ExpenseTabsScreen(
                            groupItem = grpItem,
                            expenseData =  mapOf(
                                "${ExpenseNomenclature.SELF.description} ( ${clbItem.expenses?.self?.size ?: 0} )" to (clbItem.expenses?.self
                                    ?: emptyList()),
                                "${ExpenseNomenclature.LEND.description} ( ${clbItem.expenses?.lend?.size ?: 0} )" to (clbItem.expenses?.lend
                                    ?: emptyList()),
                                "${ExpenseNomenclature.OWE.description} ( ${clbItem.expenses?.owe?.size ?: 0} )" to (clbItem.expenses?.owe
                                    ?: emptyList())
                            ),
                            performUpdateOrDeleteOperationCallback = { perform, expense ->
                                splitExpenseViewModel.performExpenseAlteringActions(
                                    perform = perform,
                                    expense = expense
                                )
                            }
                        )
                    }
                }
            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Something went wrong")
            }
        }

        updateDeleteData.takeIf { data -> data.perform == ExpenseState.CREATE }?.let {
            groupItem?.let { grpItem ->
                GenericBottomSheet(
                    sheetState = bottomSheetState,
                    headingTitleText = stringResource(R.string.add_expense),
                    buttonText = stringResource(R.string.add),
                    formInputDataFields = buildAddExpenseFormFields(grpItem = grpItem),
                    formOutputData = { data ->
                        executeExpenseCreationDataAfterFormSubmission(
                            data = data,
                            grpItem = grpItem,
                            collaboratorItem = collaboratorItem,
                            splitExpenseViewModel = splitExpenseViewModel
                        )
                    },
                    dismissSheet = {
                        coroutine.launch {
                            splitExpenseViewModel.resetUpdateOrDeleteState()
                            bottomSheetState.hide()
                        }
                    },
                    onFormFieldDone = { data ->
                        executeExpenseCreationDataAfterFormSubmission(
                            data = data,
                            grpItem = grpItem,
                            collaboratorItem = collaboratorItem,
                            splitExpenseViewModel = splitExpenseViewModel
                        )
                    }
                )
            } ?: context.showToast("Invalid req.")
        }

        updateDeleteData.takeIf { data -> data.perform == ExpenseState.UPDATE }?.let {
            groupItem?.let { grpItem ->
                updateDeleteData.expenseResponseDto?.let { expense ->
                    (grpItem to expense)
                }
            }?.let { (groupItem, expense) ->
                GenericBottomSheet(
                    sheetState = editExpenseBottomSheetState,
                    headingTitleText = stringResource(R.string.update,
                        stringResource(R.string.expense)),
                    buttonText = stringResource(R.string.update_text),
                    formInputDataFields = buildEditExpenseFormFields(
                        expense = expense,
                        grpItem = groupItem
                    ),
                    formOutputData = { data ->
                        executeExpenseUpdateDataAfterFormSubmission(
                            data = data,
                            groupItem = groupItem,
                            collaboratorItem = collaboratorItem,
                            expense = expense,
                            splitExpenseViewModel = splitExpenseViewModel
                        )
                    },
                    dismissSheet = {
                        coroutine.launch {
                            splitExpenseViewModel.resetUpdateOrDeleteState()
                            editExpenseBottomSheetState.hide()
                        }
                    },
                    onFormFieldDone = { data ->
                        executeExpenseUpdateDataAfterFormSubmission(
                            data = data,
                            groupItem = groupItem,
                            collaboratorItem = collaboratorItem,
                            expense = expense,
                            splitExpenseViewModel = splitExpenseViewModel
                        )
                    }
                )
            }
        }

        LaunchedEffect(bottomSheetState.currentValue) {
            if (bottomSheetState.currentValue == ModalBottomSheetValue.Hidden) {
                bottomSheetState.hide()
            }
        }

        with(splitExpenseViewModel) {
            val state = updateDeleteInfoState.value
            when(state.perform) {
                ExpenseState.CREATE -> {
                    coroutine.launch {
                        bottomSheetState.show()
                    }
                }

                ExpenseState.UPDATE -> {
                    coroutine.launch {
                        editExpenseBottomSheetState.show()
                    }
                }

                ExpenseState.DELETE -> {
                    CustomDialog(
                        title = "Delete Expense",
                        desc = stringResource(
                            R.string.are_you_sure_you_ant_to_delete_this,
                            "expense"
                        ),
                        confirmButtonText = stringResource(R.string.delete),
                        dismissButtonText = stringResource(R.string.cancel),
                        onConfirm = {
                            state.expenseResponseDto?.eCreationId?.let { eCreationId ->
                                splitExpenseViewModel.startExpenseChosenProcess(
                                    expenseState = ExpenseState.DELETE,
                                    expenseUpdateDeleteRequestPostData = ExpenseUpdateDeleteRequestPostData(
                                        expenseCreationId = eCreationId
                                    )
                                )
                            }
                            splitExpenseViewModel.resetUpdateOrDeleteState()
                        },
                        onDismiss = {
                            splitExpenseViewModel.resetUpdateOrDeleteState()
                        }
                    )
                }

                else -> {}
            }

            // listen to state changes for collaborator apis call
            (expenseState.value is Resource.Loading).takeIf { condition -> condition }?.let {
                LoadingUi()
            }

            (expenseState.value is Resource.Error).takeIf { condition -> condition }?.let {
                val errorMsg = expenseState.value.message
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                splitExpenseViewModel.resetExpenseState()
            }

            if(expenseState.value is Resource.Success &&
                getAllGroupDetails.value is Resource.Success) {
                expenseState.value.data?.let { successMsg ->
                    Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
                    splitExpenseViewModel.resetExpenseState()
                }
            }
        }
    }
}

fun executeExpenseCreationDataAfterFormSubmission(
    data: Map<String, String>,
    grpItem: GroupExpenseResponseDto,
    collaboratorItem: GroupExpenseResponseDto.Collaborator?,
    splitExpenseViewModel: SplitExpenseViewModel,
) {
    data.let { validRequestData ->
        grpItem.id?.let { grpId ->
            collaboratorItem?.id?.let { collaboratorId ->
                (grpId to collaboratorId)
            }
        }?.let { (grpId, collaboratorId) ->
            prepareExpenseRequest(
                value = validRequestData,
                alteringState = ExpenseAlterState.CREATE
            ).copy(
                addedByCollaboratorId = collaboratorId,
                groupId = grpId
            ).let { request ->
                splitExpenseViewModel.startExpenseChosenProcess(
                    expenseState = ExpenseState.CREATE,
                    expenseUpdateDeleteRequestPostData = ExpenseUpdateDeleteRequestPostData(
                        expenseRequestData = request
                    )
                )
            }
        }
    }
}

fun executeExpenseUpdateDataAfterFormSubmission(
    data: Map<String, String>,
    groupItem: GroupExpenseResponseDto,
    collaboratorItem: GroupExpenseResponseDto.Collaborator?,
    splitExpenseViewModel: SplitExpenseViewModel,
    expense: GroupExpenseResponseDto.Collaborator.AllExpenses.Expense
) {
    (data.let { validRequestData ->
        groupItem.id?.let { grpId ->
            collaboratorItem?.id?.let { collaboratorId ->
                (grpId to collaboratorId)
            }
        }?.let { (grpId, collaboratorId) ->
            prepareExpenseRequest(
                value = validRequestData,
                alteringState = ExpenseAlterState.UPDATE
            ).copy(
                addedByCollaboratorId = collaboratorId,
                groupId = grpId
            ).let { request ->
                if(request != ExpenseRequestDto.default()) {
                    splitExpenseViewModel.startExpenseChosenProcess(
                        expenseState = ExpenseState.UPDATE,
                        expenseUpdateDeleteRequestPostData = ExpenseUpdateDeleteRequestPostData(
                            expenseCreationId = expense.eCreationId,
                            expenseRequestData = request
                        )
                    )
                }
            }
        }
    })
}

@Composable
fun TotalExpenseDetailsUi(
    grpItem: GroupExpenseResponseDto,
    clbItem: GroupExpenseResponseDto.Collaborator
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .drawBehind {
                val stroke = Stroke(
                    width = dp1.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(
                            10f,
                            10f
                        ),
                        0f
                    )
                )
                drawRoundRect(
                    color = Color.Gray,
                    size = size,
                    cornerRadius = CornerRadius(
                        dp8.toPx(),
                        dp8.toPx()
                    ),
                    style = stroke
                )
            }
    ) {
        HeaderInfo(
            labelText = stringResource(R.string.total_expenses),
            valueText = "${grpItem.getTotalExpenseById(collaboratorId = clbItem.id)}"
        )

        VerticalDashedDivider(
            dividerColor = MaterialTheme.colorScheme.inversePrimary
        )

        HeaderInfo(
            labelText = stringResource(R.string.total_expenses_amount),
            valueText = "₹ ${grpItem.getTotalExpenseValueById(collaboratorId = clbItem.id)}"
        )
    }
}

@Composable
fun HeaderInfo(
    labelText: String,
    valueText: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(dp8)
    ) {
        Text(
            text = labelText,
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        Spacer(modifier = Modifier.height(dp2))

        Text(
            text = valueText,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.W700,
                fontSize = sp24,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun ExpenseTabsScreen(
    groupItem: GroupExpenseResponseDto,
    expenseData: Map<String,
            List<GroupExpenseResponseDto.Collaborator.AllExpenses.Expense>>,
    performUpdateOrDeleteOperationCallback: (perform: Any, expense: GroupExpenseResponseDto.Collaborator.AllExpenses.Expense) -> Unit,
    ) {
    val tabTitles = expenseData.keys.toList()

    var selectedTabIndex by remember { mutableIntStateOf(0) }


    TabRow(
        containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
        selectedTabIndex = selectedTabIndex,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = getColorForExpenseNomenclature(
                    tabTitles[selectedTabIndex]
                )
            )
        },
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { selectedTabIndex = index },
                selectedContentColor = getColorForExpenseNomenclature(title),
                unselectedContentColor = MaterialTheme.colorScheme.inversePrimary,
                text = {
                    Text(
                        title.replaceFirstChar { it.uppercaseChar() },
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            )
        }
    }

    val currentList = expenseData[tabTitles[selectedTabIndex]] ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.onSecondaryContainer)
            .padding(vertical = dp16)
    ) {

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = MaterialTheme.typography.bodyMedium.toSpanStyle().copy(
                        color = MaterialTheme.colorScheme.inversePrimary
                    )
                ) {
                    append(stringResource(R.string.total_amount_as_per_this_section, tabTitles[selectedTabIndex]))
                }
                withStyle(
                    style = MaterialTheme.typography.labelLarge.toSpanStyle().copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    append("₹${currentList.sumOf { it.eAmt }}")
                }
            })

        Spacer(modifier = Modifier.height(dp16))

        if(currentList.isEmpty()) {
            EmptyDataUi(
                imageUrl = "https://schedify.pythonanywhere.com/media/pictures/add_expense_normal.png",
                msg = stringResource(R.string.no_data_available),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = dp16),
                verticalArrangement = Arrangement.spacedBy(dp16)
            ) {
                items(currentList) { item ->
                    ExpenseCard(
                        tabType = tabTitles[selectedTabIndex],
                        item = item,
                        groupItem = groupItem,
                        performUpdateOrDeleteOperationCallback = performUpdateOrDeleteOperationCallback,
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseCard(
    tabType: String,
    item: GroupExpenseResponseDto.Collaborator.AllExpenses.Expense,
    groupItem: GroupExpenseResponseDto,
    performUpdateOrDeleteOperationCallback: (perform: Any, expense: GroupExpenseResponseDto.Collaborator.AllExpenses.Expense) -> Unit,
) {

    val isAlterOptionMenuVisible = remember { mutableStateOf(false) }

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
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomChip(
                        text = item.eExpenseType,
                        textColor = getColorForExpenseType(ExpenseType.fromDescription(item.eExpenseType)),
                        bgColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        borderColor = getColorForExpenseType(ExpenseType.fromDescription(item.eExpenseType)),
                        isOutlined = true
                    )

                    if (tabType.contains(ExpenseNomenclature.SELF.description, ignoreCase = true).not())
                        CustomChip(
                            text = stringResource(
                                R.string.to_be_settled_by_with_separator,
                                groupItem.getCollaboratorName(item.expenseForCollaborator)
                                    ?: stringResource(R.string.not_applicable)
                            ),
                            textColor = MaterialTheme.colorScheme.onPrimary,
                            bgColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            isOutlined = true,
                            borderColor = MaterialTheme.colorScheme.onPrimary,
                            outlinedBorderRadius = dp16
                        )
                    else
                        Row {
                            Icon(
                                modifier = Modifier.
                                clickable {
                                    isAlterOptionMenuVisible.value = true
                                },
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options"
                            )
                            DropdownMenu(
                                containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                expanded = isAlterOptionMenuVisible.value,
                                onDismissRequest = {
                                    isAlterOptionMenuVisible.value = false
                                }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.onBackground
                                            )
                                            Spacer(modifier = Modifier.width(
                                                dp4))
                                            Text(
                                                stringResource(
                                                    R.string.edit
                                                ),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onBackground
                                                )
                                            )
                                        }
                                    },
                                    onClick = {
                                        isAlterOptionMenuVisible.value = false
                                        performUpdateOrDeleteOperationCallback(
                                            ExpenseState.UPDATE,
                                            item
                                        )
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "",
                                                tint = MaterialTheme.colorScheme.secondary
                                            )
                                            Spacer(modifier = Modifier.width(
                                                dp4))
                                            Text(
                                                stringResource(
                                                    R.string.delete
                                                ),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            )
                                        }
                                    },
                                    onClick = {
                                        isAlterOptionMenuVisible.value = false
                                        performUpdateOrDeleteOperationCallback(
                                            ExpenseState.DELETE,
                                            item
                                        )
                                    }
                                )
                            }
                        }
                }


                Spacer(modifier = Modifier.height(dp8))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        item.eName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )

                    StaticChips(
                        text = "₹ ${item.eAmt}",
                        textStyle = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Column (
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.Start
                ) {

                    if(item.eDescription.isNotEmpty()) {
                        Text(
                            item.eDescription,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.inversePrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(dp8))
                    }

                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = MaterialTheme.typography.labelMedium.toSpanStyle().copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                        alpha = 0.6f
                                    )
                                )
                            ) {
                                append("Qty | ")
                            }
                            withStyle(
                                style = MaterialTheme.typography.labelMedium.toSpanStyle().copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                append("${item.eQty} ${item.eQtyUnit}")
                            }
                        })

                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = MaterialTheme.typography.labelMedium.toSpanStyle().copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                        alpha = 0.6f
                                    )
                                )
                            ) {
                                append("Expensed | ")
                            }
                            withStyle(
                                style = MaterialTheme.typography.labelMedium.toSpanStyle().copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                append("₹${item.eRawAmt}")
                            }
                        })
                }

                DashedDivider(
                    modifier = Modifier.fillMaxWidth(),
                    dividerColor = MaterialTheme.colorScheme.inversePrimary,
                    vSpace = dp8
                )

                Column {
                    Text(
                        text = "Added by ‣",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = 0.6f
                            )
                        )
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = MaterialTheme.typography.bodySmall.toSpanStyle().copy(
                                    color = MaterialTheme.colorScheme.inversePrimary
                                )
                            ) {
                                append("${groupItem.getCollaboratorName(item.addedByCollaboratorId)} ∙ ")
                            }
                            withStyle(
                                style = MaterialTheme.typography.bodySmall.toSpanStyle().copy(
                                    color = MaterialTheme.colorScheme.inversePrimary
                                )
                            ) {
                                append(formattedServerDateTime(item.createdOn))
                            }
                        })
                }
            }

            /*if (allExpenses[index].isSettled == true)
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
                )*/
        }
    }
}

@Composable
fun ExpenseTopBarActions(
    onButtonClick: () -> Unit
) {
    // Kept separate due to future req.
    AddExpenseButton { onButtonClick.invoke() }
}

@Composable
fun AddExpenseButton(
    onButtonClick: () -> Unit
) {
    Box(
        modifier = Modifier.padding(horizontal = dp16)
    ) {
        ActionIcons(
            modifier = Modifier
                .clip(RoundedCornerShape(dp24))
                .background(color = MaterialTheme.colorScheme.primary)
                .border(
                    BorderStroke(dp1, MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(dp24)
                ),

            iconText = stringResource(R.string.add_expense),
            textColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            onButtonClick.invoke()
        }
    }
}