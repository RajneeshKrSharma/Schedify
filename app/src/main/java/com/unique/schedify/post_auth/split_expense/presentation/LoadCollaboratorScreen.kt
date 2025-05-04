package com.unique.schedify.post_auth.split_expense.presentation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.google.gson.Gson
import com.unique.schedify.R
import com.unique.schedify.core.presentation.common_composables.CustomChip
import com.unique.schedify.core.presentation.common_composables.CustomDialog
import com.unique.schedify.core.presentation.common_composables.DashedDivider
import com.unique.schedify.core.presentation.common_composables.LoadingUi
import com.unique.schedify.core.presentation.utils.size_units.dp1
import com.unique.schedify.core.presentation.utils.size_units.dp12
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp2
import com.unique.schedify.core.presentation.utils.size_units.dp4
import com.unique.schedify.core.presentation.utils.size_units.dp60
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.util.Resource
import com.unique.schedify.post_auth.post_auth_utils.CollaboratorAlterState
import com.unique.schedify.post_auth.post_auth_utils.CollaboratorState
import com.unique.schedify.post_auth.post_auth_utils.SplitScheduleListMoreOption
import com.unique.schedify.post_auth.split_expense.data.remote.dto.CollaboratorRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import com.unique.schedify.post_auth.split_expense.presentation.utils.EmptyDataUi
import com.unique.schedify.post_auth.split_expense.presentation.utils.ExpenseNomenclature
import com.unique.schedify.post_auth.split_expense.presentation.utils.GenericBottomSheet
import com.unique.schedify.post_auth.split_expense.presentation.utils.buildAddCollaboratorFormFields
import com.unique.schedify.post_auth.split_expense.presentation.utils.buildEditCollaboratorFormFields
import com.unique.schedify.post_auth.split_expense.presentation.utils.prepareCollaboratorRequest
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun LoadCollaboratorScreen(
    navController: NavController,
    grpItem: GroupExpenseResponseDto,
    splitExpenseViewModel: SplitExpenseViewModel
) {

    val grpDetails by splitExpenseViewModel.getAllGroupDetails

    val groupId = grpItem.id
    val groupItem by remember(groupId, grpDetails) {
        derivedStateOf {
            grpDetails.data?.find { it.id == groupId }
        }
    }

    val context = LocalContext.current

    val addCollaboratorBottomSheetState = androidx.compose.material.rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    val editCollaboratorBottomSheetState = androidx.compose.material.rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val coroutine = rememberCoroutineScope()

    val updateDeleteData = splitExpenseViewModel.updateDeleteInfoState.value

    LaunchedEffect(key1 =  Unit) {
        splitExpenseViewModel.resetCollaboratorState()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SplitListScreen(
            appBarText = stringResource(R.string.collaborators),
            navController = navController,
            appBarActions = {
                CollaboratorTopBarActions(
                    addCollaboratorButtonClicked = {
                        coroutine.launch {
                            addCollaboratorBottomSheetState.show()
                        }
                    }
                )
            }
        ) {
            groupItem?.let { item ->
                CollaboratorDetailsUi(
                    grpItem = item,
                    splitExpenseViewModel = splitExpenseViewModel,
                    navController = navController,
                    editCollaboratorCallback = {
                        coroutine.launch {
                            editCollaboratorBottomSheetState.show()
                        }
                    }
                )
            } ?: EmptyDataUi(
                image = R.drawable.split_expense_icon,
                msg = "No Data Found"
            )

        }
        FilterUi()

        // Put your ui codes like dialogs / bottomSheets
        updateDeleteData.collaborator?.let { updateDeleteNotNullData ->
            GenericBottomSheet(
                sheetState = editCollaboratorBottomSheetState,
                headingTitleText = stringResource(R.string.update,
                    stringResource(R.string.collaborator)),
                buttonText = stringResource(R.string.update),
                formInputDataFields = buildEditCollaboratorFormFields(
                    collaborator = updateDeleteNotNullData
                ),
                formOutputData = { data ->
                    (data.let { validRequestData ->
                        prepareCollaboratorRequest(
                            collaborator = updateDeleteNotNullData,
                            value = validRequestData,
                            grpItem = grpItem,
                            alteringState = CollaboratorAlterState.UPDATE
                        ).let { request ->
                            if(request != CollaboratorRequestDto.empty()) {
                                splitExpenseViewModel.startCollaboratorChosenProcess(
                                    collaboratorState = CollaboratorState.UPDATE,
                                    collaboratorRequestDto = request
                                )
                            }
                        }
                    })
                },
                dismissSheet = {
                    coroutine.launch {
                        splitExpenseViewModel.resetUpdateOrDeleteState()
                        editCollaboratorBottomSheetState.hide()
                    }
                }
            )
        }

        GenericBottomSheet(
            sheetState = addCollaboratorBottomSheetState,
            headingTitleText = stringResource(R.string.add_collaborator),
            buttonText = stringResource(R.string.add),
            formInputDataFields = buildAddCollaboratorFormFields(),
            formOutputData = { data ->
                data.let { validRequestData ->
                    prepareCollaboratorRequest(
                        value = validRequestData,
                        grpItem = grpItem,
                        alteringState = CollaboratorAlterState.CREATE
                    ).let {
                            request ->
                        if(request != CollaboratorRequestDto.empty()) {
                            splitExpenseViewModel.startCollaboratorChosenProcess(
                                collaboratorState = CollaboratorState.CREATE,
                                collaboratorRequestDto = request
                            )
                        }
                    }
                }
            },
            dismissSheet = {
                coroutine.launch {
                    splitExpenseViewModel.resetUpdateOrDeleteState()
                    addCollaboratorBottomSheetState.hide()
                }
            }
        )

        with(splitExpenseViewModel) {
            // listen to state changes for collaborator apis call
            (collaboratorState.value is Resource.Loading).takeIf { condition -> condition }?.let {
                LoadingUi()
            }

            (collaboratorState.value is Resource.Error).takeIf { condition -> condition }?.let {
                collaboratorState.value.message?.let { errorMsg ->
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    splitExpenseViewModel.resetCollaboratorState()
                }
            }

            if(collaboratorState.value is Resource.Success &&
                getAllGroupDetails.value is Resource.Success) {
                collaboratorState.value.data?.let { successMsg ->
                    Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
                    splitExpenseViewModel.resetCollaboratorState()
                }

            }
        }
    }
}

@Composable
fun CollaboratorTopBarActions(
    addCollaboratorButtonClicked:(()->Unit)
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(dp60))
            .background(MaterialTheme.colorScheme.primary)
            .clickable {
                addCollaboratorButtonClicked.invoke()
            }
        ,
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(dp12),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .width(dp16)
                    .height(dp16),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer),
                painter = painterResource(R.drawable.add_collaborator),
                contentDescription = stringResource(R.string.add_collaborator))
            Spacer(modifier = Modifier.width(dp8))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text= stringResource(R.string.add),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
                Text(
                    text= stringResource(R.string.collaborator),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }
        }
    }

}

@Composable
fun CollaboratorDetailsUi(
    grpItem: GroupExpenseResponseDto,
    splitExpenseViewModel: SplitExpenseViewModel,
    navController: NavController,
    editCollaboratorCallback:() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(dp16)
            .background(
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                shape = RoundedCornerShape(dp8)
            )
    ) {
        Column(
            modifier = Modifier.padding(dp16)
        ) {
            // Header consists of grp, collaborator and Add collaborator btn.
            TopHeaderUi(
                groupItem = grpItem
            )

            DashedDivider()

            // Prepare collaborator ui
            grpItem.collaborators?.sortedBy { it?.collabUserId != splitExpenseViewModel.getAuthUserId() }
                ?.let { collaboratorList ->
                    PrepareCollaboratorListUi(
                        allCollaboratorList = collaboratorList,
                        userId = splitExpenseViewModel.getAuthUserId(),
                        performUpdateOrDeleteOperationCallback = { perform, collaborator ->
                            splitExpenseViewModel.performCollaboratorUpdateOrDelete(
                                perform, collaborator
                            )
                        },
                        performNavigationCallBack = { type, args ->
                            performNavigation(
                                navController = navController,
                                performActionFor = type,
                                args = args
                            )
                        }
                    )
                }


            with(splitExpenseViewModel) {
                val state = updateDeleteInfoState.value
                when(state.perform) {
                    CollaboratorState.UPDATE -> {
                        editCollaboratorCallback.invoke()
                    }

                   CollaboratorState.DELETE -> {

                       val isAnyExpenseBelongToSelectedCollaborator =
                           state.collaborator?.expenses?.owe?.isNotEmpty() == true

                        CustomDialog(
                            title = stringResource(R.string.delete_collaborator),
                            desc = stringResource(
                                R.string.are_you_sure_you_want_to_delete_this_collaborator,
                                stringResource(R.string.since_this_collaborator_s_expenses_aren_t_settled)
                                    .takeIf { isAnyExpenseBelongToSelectedCollaborator } ?: "",
                            ),
                            confirmButtonText = stringResource(R.string.delete),
                            dismissButtonText = stringResource(R.string.cancel),
                            onConfirm = {
                                state.collaborator?.id?.let { collaboratorId ->
                                    splitExpenseViewModel.startCollaboratorChosenProcess(
                                        collaboratorState = CollaboratorState.DELETE,
                                        collaboratorRequestDto = CollaboratorRequestDto.empty().copy(
                                            groupId = grpItem.id,
                                            collaboratorId = collaboratorId
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
            }
        }
    }
}

@Composable
fun PrepareCollaboratorListUi(
    allCollaboratorList: List<GroupExpenseResponseDto.Collaborator?>,
    userId: Int,
    performUpdateOrDeleteOperationCallback: (perform: Any, collaborator: GroupExpenseResponseDto.Collaborator) -> Unit,
    performNavigationCallBack:(type: SplitScheduleListMoreOption, args: Any) -> Unit
) {
    allCollaboratorList.filterNotNull().let { allCollaborator ->
        LazyColumn(
            contentPadding = PaddingValues(vertical = dp16),
            verticalArrangement = Arrangement.spacedBy(dp16)
        ) {
            items(allCollaborator.size) { index ->
                val isAllowedDrillDown = allCollaborator[index].collabUserId == userId
                val isEditOptionMenuVisible = remember { mutableStateOf(false) }
                val isDeleteOptionMenuVisible = remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(dp8),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = dp4
                    ),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clip(RoundedCornerShape(dp8))
                            .clickable {
                                val collaboratorJson =
                                    Gson().toJson(allCollaborator[index])
                                val encodedCollaboratorJson =
                                    URLEncoder.encode(
                                        collaboratorJson,
                                        StandardCharsets.UTF_8.toString()
                                    )

                                performNavigationCallBack(
                                    SplitScheduleListMoreOption.EXPENSES_SCREEN,
                                    encodedCollaboratorJson
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dp16),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        allCollaborator[index].status?.let { status ->
                                            CustomChip(
                                                text = status,
                                                textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                                bgColor = MaterialTheme.colorScheme.secondary.copy(
                                                    alpha = 0.85f
                                                )
                                            )
                                        }

                                        if(isAllowedDrillDown) {
                                            Row {
                                                Icon(
                                                    modifier = Modifier.
                                                    clickable { isEditOptionMenuVisible.value = true },
                                                    imageVector = Icons.Default.MoreVert,
                                                    contentDescription = "More options"
                                                )
                                                DropdownMenu(
                                                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    expanded = isEditOptionMenuVisible.value,
                                                    onDismissRequest = {
                                                        isEditOptionMenuVisible.value = false
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
                                                            isEditOptionMenuVisible.value = false
                                                            performUpdateOrDeleteOperationCallback(
                                                                CollaboratorState.UPDATE,
                                                                allCollaborator[index]
                                                            )
                                                        }
                                                    )
                                                }
                                            }
                                        } else {
                                            Row {
                                                Icon(
                                                    modifier = Modifier.
                                                    clickable { isDeleteOptionMenuVisible.value = true },
                                                    imageVector = Icons.Default.MoreVert,
                                                    contentDescription = "More options"
                                                )
                                                DropdownMenu(
                                                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    expanded = isDeleteOptionMenuVisible.value,
                                                    onDismissRequest = {
                                                        isDeleteOptionMenuVisible.value = false }
                                                ) {
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
                                                            isDeleteOptionMenuVisible.value = false
                                                            performUpdateOrDeleteOperationCallback(
                                                                CollaboratorState.DELETE,
                                                                allCollaborator[index]
                                                            )
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(dp16))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        allCollaborator[index].collabName(),
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(dp16))


                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
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
                                        .padding(dp8),
                                ) {
                                    Text(
                                        stringResource(R.string.your_expenses_are_listed_below),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )

                                    Spacer(modifier = Modifier.padding(bottom = dp8))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(IntrinsicSize.Min),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(dp4),
                                            text = ExpenseNomenclature.SELF.description,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = MaterialTheme.colorScheme.inversePrimary
                                            ),
                                        )

                                        Text(
                                            modifier = Modifier.padding(dp4),
                                            text = stringResource(
                                                R.string.rupee_symbol,
                                                allCollaborator[index].expenses?.self?.sumOf { it.eAmt }
                                                    ?: 0),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            ),
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(dp2))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(IntrinsicSize.Min),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(dp4),
                                            text = ExpenseNomenclature.LEND.description,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = MaterialTheme.colorScheme.inversePrimary
                                            ),
                                        )

                                        Text(
                                            modifier = Modifier.padding(dp4),
                                            text = stringResource(
                                                R.string.rupee_symbol,
                                                allCollaborator[index].expenses?.lend?.sumOf { it.eAmt } ?: 0),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            ),
                                            textAlign = TextAlign.Start
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(dp2))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(IntrinsicSize.Min),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            modifier = Modifier.padding(dp4),
                                            text = ExpenseNomenclature.OWE.description,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = MaterialTheme.colorScheme.inversePrimary
                                            ),
                                        )

                                        Text(
                                            modifier = Modifier.padding(dp4),
                                            text = stringResource(
                                                R.string.rupee_symbol,
                                                allCollaborator[index].expenses?.owe?.sumOf { it.eAmt } ?: 0),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            ),
                                        )
                                    }

                                }
                            }
                        }

                        allCollaborator[index].isActive?.let { isActive ->
                            if (isActive.not()) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(
                                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(
                                                alpha = 0.7f
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            stringResource(R.string.i_n_v_i_t_e_d),
                                            style = MaterialTheme.typography.displayLarge.copy(
                                                fontWeight = FontWeight.W600,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        )
                                        Text(
                                            stringResource(R.string.this_will_be_enabled_once_this_collaborator_login_and_check_his_group_list,
                                                allCollaborator[index].collabEmailId
                                                    ?: "N/A"
                                            ),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopHeaderUi(
    groupItem: GroupExpenseResponseDto
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start

    ) {
        Row {
            groupItem.name?.let { name ->
                CustomChip(
                    text = stringResource(R.string.group_with_separator, name),
                    textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    bgColor = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }

            Spacer(modifier = Modifier.padding(horizontal = dp4))

            CustomChip(
                text = stringResource(
                    R.string.collaborator_with_separator,
                    groupItem.getTotalCollaborator()
                ),
                textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                bgColor = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    }
}

@Composable
fun FilterUi() {
    /*
        val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val drawerWidth = screenWidth * 0.85f
    val offsetX = animateDpAsState(targetValue = if (drawerVisible.value) dp0 else drawerWidth)

    // Collaborator Filter UI
        if (drawerVisible.value) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable {
                        drawerVisible.value = false
                    }
            )
        }
    Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(drawerWidth)
                .offset { IntOffset(offsetX.value.roundToPx(), 0) }
                .align(Alignment.CenterEnd)
                .background(MaterialTheme.colorScheme.surface)
                .zIndex(2f)
        ) {
            Column(
                modifier = Modifier
                    .padding(dp12)
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            ) {
                Text(
                    stringResource(R.string.filter),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(dp16))

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dp16)
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())) {
                        FormBuilder(fields = formFields.value) { formData ->
                            println("Form Data = $formData")
                            formResultedData.value = formData
                        }
                         GradientButton(
                             text = stringResource(R.string.apply_filter),
                             enabled = isFormValid
                         ) {
                             drawerVisible.value = false
                         }
                    }
                }
            }
        }
    * */
}