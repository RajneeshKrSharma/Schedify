package com.unique.schedify.post_auth.split_expense.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unique.schedify.core.config.SharedPrefConfig
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.core.util.Resource
import com.unique.schedify.post_auth.split_expense.data.remote.dto.CollaboratorRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import com.unique.schedify.post_auth.split_expense.domain.use_case.GetGroupExpenseUseCase
import com.unique.schedify.post_auth.split_expense.domain.use_case.SaveCollaboratorUseCase
import com.unique.schedify.post_auth.split_expense.domain.use_case.SaveGroupUseCase
import com.unique.schedify.post_auth.post_auth_utils.CollaboratorState
import com.unique.schedify.post_auth.post_auth_utils.ExpenseState
import com.unique.schedify.post_auth.post_auth_utils.GroupState
import com.unique.schedify.post_auth.split_expense.data.remote.dto.ExpenseUpdateDeleteRequestPostData
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto.Collaborator
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupUpdateDeleteRequestPostData
import com.unique.schedify.post_auth.split_expense.data.remote.other.UpdateDeleteInfoModel
import com.unique.schedify.post_auth.split_expense.domain.use_case.DeleteCollaboratorUseCase
import com.unique.schedify.post_auth.split_expense.domain.use_case.DeleteGroupUseCase
import com.unique.schedify.post_auth.split_expense.domain.use_case.SaveExpenseUseCase
import com.unique.schedify.post_auth.split_expense.domain.use_case.UpdateCollaboratorUseCase
import com.unique.schedify.post_auth.split_expense.domain.use_case.UpdateGroupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplitExpenseViewModel @Inject constructor(
    private val getGroupExpenseUseCase: GetGroupExpenseUseCase,
    private val saveGroupUseCase: SaveGroupUseCase,
    private val saveCollaboratorUseCase: SaveCollaboratorUseCase,
    private val updateCollaboratorUseCase: UpdateCollaboratorUseCase,
    private val deleteCollaboratorUseCase: DeleteCollaboratorUseCase,
    private val saveExpenseUseCase: SaveExpenseUseCase,
    private val sharedPrefConfig: SharedPrefConfig,
    private val updateGroupUseCase: UpdateGroupUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,
) : ViewModel() {
    private val _getAllGroupDetails = mutableStateOf<Resource<List<GroupExpenseResponseDto>>>(Resource.Default())
    val getAllGroupDetails: State<Resource<List<GroupExpenseResponseDto>>> = _getAllGroupDetails

    private val _getCollaboratorDetails = mutableStateOf<Resource<List<Collaborator>>>(Resource.Default())
    val getCollaboratorDetails: State<Resource<List<Collaborator>>> = _getCollaboratorDetails


    private val _groupState = mutableStateOf<Resource<String>>(Resource.Default())
    val groupState: State<Resource<String>> = _groupState

    private val _collaboratorState = mutableStateOf<Resource<String>>(Resource.Default())
    val collaboratorState: State<Resource<String>> = _collaboratorState

    private val _expenseState = mutableStateOf<Resource<String>>(Resource.Default())
    val expenseState: State<Resource<String>> = _expenseState

    private val _updateDeleteInfoState = mutableStateOf(UpdateDeleteInfoModel.empty())
    val updateDeleteInfoState: State<UpdateDeleteInfoModel> = _updateDeleteInfoState


    init {
        viewModelScope.launch {
            _getAllGroupDetails.value = Resource.Loading()
            
            with(getGroupExpenseUseCase.execute()) {
                when (this) {
                    is ApiResponseResource.Success -> {
                        _getAllGroupDetails.value = Resource.Success(this.data)
                    }

                    is ApiResponseResource.Error -> {
                        _getAllGroupDetails.value = Resource.Error(this.errorMessage)
                    }
                }
            }
        }
    }

    fun startGroupChosenProcess(
        groupState: GroupState,
        groupUpdateDeleteRequestPostData: GroupUpdateDeleteRequestPostData
    ) {
        viewModelScope.launch {
            when(groupState) {
                GroupState.CREATE -> {
                    _groupState.value = Resource.Loading()
                    
                    with(saveGroupUseCase.execute(args = groupUpdateDeleteRequestPostData.groupRequestData)) {
                        when (this) {
                            is ApiResponseResource.Success -> {
                                _getAllGroupDetails.value = Resource.Success(this.data)
                                _groupState.value = Resource.Success("Group Created Successfully")
                            }

                            is ApiResponseResource.Error -> {
                                _groupState.value = Resource.Error(this.errorMessage)
                            }
                        }
                    }
                }
                GroupState.UPDATE -> {
                    _groupState.value = Resource.Loading()
                    
                    with(updateGroupUseCase.execute(
                        args = groupUpdateDeleteRequestPostData
                    )) {
                        when (this) {
                            is ApiResponseResource.Success -> {
                                
                                getSplitExpenseData()
                                _groupState.value = Resource.Success("Group Updated Successfully")
                            }

                            is ApiResponseResource.Error -> {
                                _groupState.value = Resource.Error(this.errorMessage)
                            }
                        }
                    }
                }
                GroupState.DELETE -> {
                    _groupState.value = Resource.Loading()
                    
                    with(deleteGroupUseCase.execute(
                        args = groupUpdateDeleteRequestPostData.id
                    )) {
                        when (this) {
                            is ApiResponseResource.Success -> {
                                
                                getSplitExpenseData()
                                _groupState.value = Resource.Success("Group Deleted Successfully")
                            }

                            is ApiResponseResource.Error -> {
                                _groupState.value = Resource.Error(this.errorMessage)
                            }
                        }
                    }

                }
                GroupState.DEFAULT -> TODO()
            }
        }
    }

    fun startCollaboratorChosenProcess(
        collaboratorState: CollaboratorState,
        collaboratorRequestDto: CollaboratorRequestDto
    ) {

        viewModelScope.launch {
            when(collaboratorState) {
                CollaboratorState.CREATE -> {
                    _collaboratorState.value = Resource.Loading()
                    
                    with(saveCollaboratorUseCase.execute(
                        collaboratorRequestDto
                    )) {
                        when (this) {
                            is ApiResponseResource.Success -> {
                                
                                getSplitExpenseData()
                                _collaboratorState.value = Resource.Success("Collaborator Created Successfully")
                            }

                            is ApiResponseResource.Error -> {
                                _collaboratorState.value = Resource.Error(this.errorMessage)
                            }
                        }
                    }
                }
                CollaboratorState.UPDATE -> {
                    _collaboratorState.value = Resource.Loading()
                    
                    with(updateCollaboratorUseCase.execute(
                        collaboratorRequestDto
                    )) {
                        when (this) {
                            is ApiResponseResource.Success -> {
                                
                                getSplitExpenseData()
                                _collaboratorState.value = Resource.Success("Collaborator Updated Successfully")
                            }

                            is ApiResponseResource.Error -> {
                                _collaboratorState.value = Resource.Error(this.errorMessage)
                            }
                        }
                    }
                }
                CollaboratorState.DELETE -> {
                    _collaboratorState.value = Resource.Loading()
                    
                    with(deleteCollaboratorUseCase.execute(
                        collaboratorRequestDto
                    )) {
                        when (this) {
                            is ApiResponseResource.Success -> {
                                
                                getSplitExpenseData()
                                _collaboratorState.value = Resource.Success("Collaborator Deleted Successfully")
                            }

                            is ApiResponseResource.Error -> {
                                _collaboratorState.value = Resource.Error(this.errorMessage)
                            }
                        }
                    }
                }
                CollaboratorState.DEFAULT -> TODO()
            }
        }
    }

    fun startExpenseChosenProcess(
        expenseState: ExpenseState,
        expenseUpdateDeleteRequestPostData: ExpenseUpdateDeleteRequestPostData? = null
    ) {

        viewModelScope.launch {
            when(expenseState) {
                ExpenseState.CREATE -> {
                    _expenseState.value = Resource.Loading()
                    
                    with(saveExpenseUseCase.execute(
                        expenseUpdateDeleteRequestPostData?.expenseRequestData
                    )) {
                        when (this) {
                            is ApiResponseResource.Success -> {
                                
                                getSplitExpenseData()
                                _expenseState.value = Resource.Success("Expense Created Successfully")
                            }

                            is ApiResponseResource.Error -> {
                                _expenseState.value = Resource.Error(this.errorMessage)
                            }
                        }
                    }
                }
                ExpenseState.UPDATE -> {
                    /*_expenseState.value = Resource.Loading()
                    with(updateExpenseUseCase.execute(
                        args = expenseUpdateDeleteRequestPostData
                    )) {
                        when (this) {
                            is ApiResponseResource.Success -> {
                                getSplitExpenseData()
                            }

                            is ApiResponseResource.Error -> {
                                _groupState.value = Resource.Error(this.errorMessage)
                            }
                        }
                    }*/
                }
                ExpenseState.DELETE -> {
                    _expenseState.value = Resource.Loading()
                    with(deleteGroupUseCase.execute(
                        args = expenseUpdateDeleteRequestPostData?.id
                    )) {
                        when (this) {
                            is ApiResponseResource.Success -> {
                                getSplitExpenseData()
                                _expenseState.value = Resource.Success("")
                            }

                            is ApiResponseResource.Error -> {
                                _groupState.value = Resource.Error(this.errorMessage)
                            }
                        }
                    }
                }
                ExpenseState.DEFAULT -> TODO()
            }
        }
    }

    private suspend fun getSplitExpenseData() {
        with(getGroupExpenseUseCase.execute()) {
            when (this) {
                is ApiResponseResource.Success -> {
                    _getAllGroupDetails.value = Resource.Success(this.data)
                }

                is ApiResponseResource.Error -> {
                    _getAllGroupDetails.value = Resource.Error(this.errorMessage)
                }
            }
        }
    }

    fun resetCollaboratorState() {
        _collaboratorState.value  = Resource.Default()
    }

    fun resetGroupState() {
        _groupState.value  = Resource.Default()
    }

    fun resetExpenseState() {
        _expenseState.value  = Resource.Default()
    }

    fun getAuthUserId(): Int = sharedPrefConfig.getAuthUserId()

    /*fun searchCollaboratorsInSelectedGroup(query: String, selectedGroup: GroupExpenseResponseDto?) {
        if (selectedGroup == null) return

        if (query.isBlank()) {
            _getAllGroupDetails.value = Resource.Success(listOf(selectedGroup))
            return
        }

        val filteredCollaborators = selectedGroup.collaborators?.filter { collab ->
            collab?.collaboratorName?.contains(query, ignoreCase = true) == true ||
                    collab?.collabEmailId?.contains(query, ignoreCase = true) == true
        }

        val updatedGroup = selectedGroup.copy(collaborators = filteredCollaborators)

        _getAllGroupDetails.value = Resource.Success(listOf(updatedGroup))

        Log.d("Rajneesh", "Filtered Collaborators: $filteredCollaborators")
    }*/

    fun performCollaboratorUpdateOrDelete(
        perform: Any,
        collaborator: Collaborator
    ) {
        _updateDeleteInfoState.value = UpdateDeleteInfoModel(
            perform = perform,
            collaborator = collaborator
        )
    }

    fun performGroupUpdateOrDelete(
        perform: Any,
        groupExpenseResponseDto: GroupExpenseResponseDto
    ) {
        _updateDeleteInfoState.value = UpdateDeleteInfoModel(
            perform = perform,
            groupExpenseResponseDto = groupExpenseResponseDto
        )
    }

    fun performExpenseUpdateOrDelete(
        perform: Any,
        expense: Collaborator.AllExpenses.Expense
    ) {
        _updateDeleteInfoState.value = UpdateDeleteInfoModel(
            perform = perform,
            expenseResponseDto = expense
        )
    }

    fun resetUpdateOrDeleteState() {
        _updateDeleteInfoState.value = UpdateDeleteInfoModel.empty()
    }
}

