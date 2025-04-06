package com.unique.schedify.post_auth.split_expense.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.core.util.Resource
import com.unique.schedify.post_auth.split_expense.data.remote.dto.CollaboratorRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupRequestDto
import com.unique.schedify.post_auth.split_expense.domain.use_case.GetGroupExpenseUseCase
import com.unique.schedify.post_auth.split_expense.domain.use_case.SaveCollaboratorUseCase
import com.unique.schedify.post_auth.split_expense.domain.use_case.SaveGroupUseCase
import com.unique.schedify.post_auth.post_auth_utils.CollaboratorState
import com.unique.schedify.post_auth.post_auth_utils.ExpenseState
import com.unique.schedify.post_auth.post_auth_utils.GroupState
import com.unique.schedify.post_auth.post_auth_utils.GroupViewMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplitExpenseViewModel @Inject constructor(
    private val getGroupExpenseUseCase: GetGroupExpenseUseCase,
    private val saveGroupUseCase: SaveGroupUseCase,
    private val saveCollaboratorUseCase: SaveCollaboratorUseCase
) : ViewModel() {
    private val _getAllGroupDetails = mutableStateOf<Resource<GroupExpenseResponseDto>>(Resource.Default())
    val getAllGroupDetails: State<Resource<GroupExpenseResponseDto>> = _getAllGroupDetails

    private val _groupState = mutableStateOf<Resource<Boolean>>(Resource.Default())
    val groupState: State<Resource<Boolean>> = _groupState

    private val _collaboratorState = mutableStateOf<Resource<Boolean>>(Resource.Default())
    val collaboratorState: State<Resource<Boolean>> = _collaboratorState

    private val _expenseState = mutableStateOf(ExpenseState.DEFAULT)
    val expenseState: State<ExpenseState> = _expenseState

    private val _groupViewModeState = mutableStateOf(GroupViewMode.COLLABORATOR_LIST)
    val groupViewModeState: State<GroupViewMode> = _groupViewModeState

    private val _getRestoreAllGroupDetails = mutableStateOf<Resource<GroupExpenseResponseDto>>(Resource.Default())
    val getRestoreAllGroupDetails: State<Resource<GroupExpenseResponseDto>> = _getRestoreAllGroupDetails


    init {
        viewModelScope.launch {
            _getAllGroupDetails.value = Resource.Loading()
            with(getGroupExpenseUseCase.execute()) {
                when (this) {
                    is ApiResponseResource.Success -> {
                        _getAllGroupDetails.value = Resource.Success(this.data)
                        _getRestoreAllGroupDetails.value = Resource.Success(this.data)
                    }

                    is ApiResponseResource.Error -> {
                        _getAllGroupDetails.value = Resource.Error(this.errorMessage)
                    }
                }
            }
        }
    }

    fun groupViewModeFilter(groupViewModeState: GroupViewMode) {
        _groupViewModeState.value = groupViewModeState
    }

    fun showExpensesCollaboratorWise(groupViewModeState: GroupViewMode, collaboratorId: Int) {
        _groupViewModeState.value = groupViewModeState
        val matchedGroup = _getAllGroupDetails.value.data?.data?.firstOrNull { it?.collaborators?.any { collaborator -> collaborator?.id == collaboratorId } ?: false }

        matchedGroup?.let {
            _getAllGroupDetails.value = Resource.Success(GroupExpenseResponseDto(data = listOf(it), message = ""))
        }}

    fun startGroupChosenProcess(
        groupState: GroupState,
        groupName: String
    ) {
        viewModelScope.launch {
            when(groupState) {
                GroupState.CREATE -> {
                    _groupState.value = Resource.Loading()
                    with(saveGroupUseCase.execute(GroupRequestDto(grpName = groupName))) {
                        when (this) {
                            is ApiResponseResource.Success -> {
                                _groupState.value = Resource.Success(true)
                                _getAllGroupDetails.value = Resource.Success(this.data)
                            }

                            is ApiResponseResource.Error -> {
                                _groupState.value = Resource.Error(this.errorMessage)
                            }
                        }
                    }
                }
                GroupState.UPDATE -> TODO()
                GroupState.DELETE -> TODO()
                GroupState.READ -> TODO()
                GroupState.DEFAULT -> TODO()
            }
        }
    }

    fun startCollaboratorChosenProcess(
        collaboratorState: CollaboratorState,
        collaboratorName: String? = null,
        groupId: Int
    ) {

        viewModelScope.launch {
            when(collaboratorState) {
                CollaboratorState.CREATE -> {
                    _collaboratorState.value = Resource.Loading()
                    with(saveCollaboratorUseCase.execute(
                        CollaboratorRequestDto(
                            collaboratorName = collaboratorName ?: "N/A",
                            groupId = groupId
                        )
                    )) {
                        when (this) {
                            is ApiResponseResource.Success -> {
                                _collaboratorState.value = Resource.Success(true)
                                _getAllGroupDetails.value = Resource.Success(this.data)
                            }

                            is ApiResponseResource.Error -> {
                                _collaboratorState.value = Resource.Error(this.errorMessage)
                            }
                        }
                    }
                }
                CollaboratorState.UPDATE -> TODO()
                CollaboratorState.DELETE -> TODO()
                CollaboratorState.READ -> TODO()
                CollaboratorState.DEFAULT -> TODO()
            }
        }
    }
}

