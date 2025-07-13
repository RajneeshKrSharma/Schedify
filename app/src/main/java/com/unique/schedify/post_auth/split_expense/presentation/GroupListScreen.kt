package com.unique.schedify.post_auth.split_expense.presentation

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.google.gson.Gson
import com.unique.schedify.R
import com.unique.schedify.core.presentation.common_composables.ActionIcons
import com.unique.schedify.core.presentation.common_composables.CustomDialog
import com.unique.schedify.core.presentation.common_composables.LoadingUi
import com.unique.schedify.core.presentation.common_composables.StaticChips
import com.unique.schedify.core.presentation.utils.size_units.dp1
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp24
import com.unique.schedify.core.presentation.utils.size_units.dp4
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.util.Resource
import com.unique.schedify.post_auth.post_auth_utils.GroupAlterState
import com.unique.schedify.post_auth.post_auth_utils.GroupState
import com.unique.schedify.post_auth.post_auth_utils.SplitScheduleListMoreOption
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupUpdateDeleteRequestPostData
import com.unique.schedify.post_auth.split_expense.presentation.utils.EmptyDataUi
import com.unique.schedify.post_auth.split_expense.presentation.utils.GenericBottomSheet
import com.unique.schedify.post_auth.split_expense.presentation.utils.buildAddGroupFormFields
import com.unique.schedify.post_auth.split_expense.presentation.utils.buildUpdateGroupFormFields
import com.unique.schedify.post_auth.split_expense.presentation.utils.prepareGroupRequest
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun GroupListScreen(
    navController: NavController,
    splitExpenseViewModel: SplitExpenseViewModel
) {
    val context = LocalContext.current
    val stateGroupDetails = splitExpenseViewModel.getAllGroupDetails.value

    val addGroupBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { newValue ->
            newValue != ModalBottomSheetValue.Hidden
        }
    )

    val updateGroupBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { newValue ->
            newValue != ModalBottomSheetValue.Hidden
        }
    )

    val coroutine = rememberCoroutineScope()

    val updateDeleteData = splitExpenseViewModel.updateDeleteInfoState.value

    SplitListScreen(
        appBarText = stringResource(R.string.groups),
        navController = navController,
        appBarActions = {
            stateGroupDetails.data?.takeIf { data -> data.isNotEmpty() }?.let {
                GroupTopBarActions(
                    onAddGroupButtonClick = {
                        splitExpenseViewModel.performGroupAlteringActions(
                            GroupState.CREATE,
                        )
                    }
                )
            }
        }
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (stateGroupDetails is Resource.Error) {
                Toast.makeText(context, stateGroupDetails.message, Toast.LENGTH_SHORT).show()
            }

            if (stateGroupDetails is Resource.Success) {
                val listOfGroups = stateGroupDetails.data ?: emptyList()
                if (listOfGroups.isEmpty()) {
                    EmptyDataUi(
                        imageUrl = "https://schedify.pythonanywhere.com/media/pictures/add_group.png",
                        msg = stringResource(
                            R.string.no_available_add_your_first,
                            "group",
                            "group"
                        ),
                        content = {
                            AddGroupButton {
                                splitExpenseViewModel.performGroupAlteringActions(
                                    GroupState.CREATE,
                                )
                            }
                        }
                    )

                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(dp16),
                            verticalArrangement = Arrangement.spacedBy(dp16)
                        ) {
                            items(listOfGroups.size) { index ->
                                val isAlterOptionMenuVisible = remember { mutableStateOf(false) }
                                val isOwner = remember {
                                    splitExpenseViewModel.getAuthUserId() == listOfGroups[index].createdBy
                                }

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
                                                val groupJson = Gson().toJson(listOfGroups[index])
                                                val encodedGroupJson = URLEncoder.encode(
                                                    groupJson,
                                                    StandardCharsets.UTF_8.toString()
                                                )

                                                performNavigation(
                                                    navController = navController,
                                                    performActionFor = SplitScheduleListMoreOption.COLLABORATOR_SCREEN,
                                                    args = encodedGroupJson
                                                )
                                            }
                                            .padding(dp16)
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.SpaceEvenly,
                                            horizontalAlignment = Alignment.Start,
                                        ) {

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    listOfGroups[index].name
                                                        ?: stringResource(R.string.not_applicable),
                                                    style = MaterialTheme.typography.titleLarge.copy(
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                )

                                                if(isOwner) {
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
                                                                    splitExpenseViewModel.performGroupAlteringActions(
                                                                        GroupState.UPDATE,
                                                                        groupExpenseResponseDto = listOfGroups[index]
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
                                                                    splitExpenseViewModel.performGroupAlteringActions(
                                                                        GroupState.DELETE,
                                                                        groupExpenseResponseDto = listOfGroups[index]
                                                                    )
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(dp16))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Start
                                            ) {
                                                StaticChips(
                                                    text = "${stringResource(R.string.collaborators)} | ${listOfGroups[index].getTotalCollaborator()}",
                                                    textStyle = MaterialTheme.typography.labelMedium.copy(
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                                    ),
                                                    color = MaterialTheme.colorScheme.tertiary,
                                                )

                                                Spacer(modifier = Modifier.width(dp16))

                                                StaticChips(
                                                    text = "${stringResource(R.string.expenses)} | ${listOfGroups[index].getTotalExpense()}",
                                                    textStyle = MaterialTheme.typography.labelMedium.copy(
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                                    ),
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
                with(splitExpenseViewModel) {
                    val state = updateDeleteInfoState.value
                    when(state.perform) {
                        GroupState.CREATE -> {
                            coroutine.launch {
                                addGroupBottomSheetState.show()
                            }
                        }

                        GroupState.UPDATE -> {
                            coroutine.launch {
                                updateGroupBottomSheetState.show()
                                updateGroupBottomSheetState.show()
                            }
                        }

                        GroupState.DELETE -> {
                            CustomDialog(
                                title = stringResource(R.string.delete_collaborator),
                                desc = stringResource(
                                    R.string.are_you_sure_you_ant_to_delete_this,
                                    "group"
                                ),
                                confirmButtonText = stringResource(R.string.delete),
                                dismissButtonText = stringResource(R.string.cancel),
                                onConfirm = {
                                    state.groupExpenseResponseDto?.id?.let { groupId ->
                                        splitExpenseViewModel.startGroupChosenProcess(
                                            groupState = GroupState.DELETE,
                                            groupUpdateDeleteRequestPostData = GroupUpdateDeleteRequestPostData(
                                                id = groupId,
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

                    // listen to state changes for groups apis call
                    (groupState.value is Resource.Loading).takeIf { condition -> condition }?.let {
                        LoadingUi()
                    }

                    (groupState.value is Resource.Error).takeIf { condition -> condition }?.let {
                        val errorMsg = groupState.value.message
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                        splitExpenseViewModel.resetGroupState()
                    }

                    if(groupState.value is Resource.Success &&
                        getAllGroupDetails.value is Resource.Success) {
                        groupState.value.data?.let { successMsg ->
                            Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
                            splitExpenseViewModel.resetGroupState()
                        }
                    }
                }
            }

            updateDeleteData.takeIf { data -> data.perform == GroupState.CREATE }?.let {
                GenericBottomSheet(
                    sheetState = addGroupBottomSheetState,
                    headingTitleText = stringResource(R.string.add_group),
                    buttonText = "Add",
                    formInputDataFields = buildAddGroupFormFields(),
                    formOutputData = { data ->
                        executeGroupCreationDataAfterFormSubmission(
                            data = data,
                            splitExpenseViewModel = splitExpenseViewModel
                        )
                    },
                    dismissSheet = {
                        coroutine.launch {
                            splitExpenseViewModel.resetUpdateOrDeleteState()
                            addGroupBottomSheetState.hide()
                        }
                    },
                    onFormFieldDone = { data ->
                        executeGroupCreationDataAfterFormSubmission(
                            data = data,
                            splitExpenseViewModel = splitExpenseViewModel
                        )
                    }
                )
            }

            updateDeleteData.takeIf { data -> data.perform == GroupState.UPDATE }?.let {
                updateDeleteData.groupExpenseResponseDto?.let { updateDeleteNotNullData ->
                    GenericBottomSheet(
                        sheetState = updateGroupBottomSheetState,
                        headingTitleText = stringResource(R.string.update,
                            stringResource(R.string.group_simple)),
                        buttonText = stringResource(R.string.update),
                        formInputDataFields = buildUpdateGroupFormFields(updateDeleteNotNullData),
                        formOutputData = { data ->
                            executeGroupUpdateDataAfterFormSubmission(
                                data = data,
                                grpItem = updateDeleteNotNullData,
                                splitExpenseViewModel = splitExpenseViewModel
                            )
                        },
                        dismissSheet = {
                            coroutine.launch {
                                splitExpenseViewModel.resetUpdateOrDeleteState()
                                updateGroupBottomSheetState.hide()
                            }
                        },
                        onFormFieldDone = { data ->
                            executeGroupUpdateDataAfterFormSubmission(
                                data = data,
                                grpItem = updateDeleteNotNullData,
                                splitExpenseViewModel = splitExpenseViewModel
                            )
                        }
                    )
                }
            }
        }

        if(stateGroupDetails is Resource.Loading) {
            LoadingUi()
        }
    }
}

fun executeGroupCreationDataAfterFormSubmission(
    data: Map<String, String>,
    splitExpenseViewModel: SplitExpenseViewModel,
) {
    data.let { validRequestData ->
        prepareGroupRequest(
            value = validRequestData,
            alteringState = GroupAlterState.CREATE
        ).let { request ->
            if(request != GroupRequestDto.empty()) {
                splitExpenseViewModel.startGroupChosenProcess(
                    groupState = GroupState.CREATE,
                    groupUpdateDeleteRequestPostData = GroupUpdateDeleteRequestPostData(
                        groupRequestData = request
                    )
                )
            }
        }
    }
}

fun executeGroupUpdateDataAfterFormSubmission(
    data: Map<String, String>,
    grpItem: GroupExpenseResponseDto,
    splitExpenseViewModel: SplitExpenseViewModel,
) {
    data.let { validRequestData ->
        prepareGroupRequest(
            value = validRequestData,
            alteringState = GroupAlterState.UPDATE
        ).let { request ->
            if(request != GroupRequestDto.empty()) {
                splitExpenseViewModel.startGroupChosenProcess(
                    groupState = GroupState.UPDATE,
                    groupUpdateDeleteRequestPostData = GroupUpdateDeleteRequestPostData(
                        id = grpItem.id,
                        groupRequestData = request
                    )
                )
            }
        }
    }
}


@Composable
fun GroupTopBarActions(
    onAddGroupButtonClick:() -> Unit
) {
    AddGroupButton(onAddGroupButtonClick)
}

@Composable
fun AddGroupButton(
    onAddGroupButtonClick:() -> Unit
) {
    ActionIcons(
        modifier = Modifier
            .padding(horizontal = dp16)
            .clip(RoundedCornerShape(dp24))
            .background(color = MaterialTheme.colorScheme.primary)
            .border(
                BorderStroke(dp1, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(dp24)
            ),

        iconText = stringResource(R.string.add_group),
        textColor = MaterialTheme.colorScheme.onSecondaryContainer,
        onClick = onAddGroupButtonClick
    )
}