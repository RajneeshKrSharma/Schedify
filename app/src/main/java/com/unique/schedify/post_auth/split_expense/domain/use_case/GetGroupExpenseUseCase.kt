package com.unique.schedify.post_auth.split_expense.domain.use_case

import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import com.unique.schedify.post_auth.split_expense.domain.repository.SplitExpenseRepository
import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupUpdateDeleteRequestPostData
import javax.inject.Inject

class GetGroupExpenseUseCase @Inject constructor(
    private val repository: SplitExpenseRepository,
    ):ApiUseCase<ApiResponseResource<List<GroupExpenseResponseDto>>, Unit> {
    override suspend fun execute(args: Unit?): ApiResponseResource<List<GroupExpenseResponseDto>> {
        return catchWrapper { repository.getGroupExpense() }
    }
}

class SaveGroupUseCase @Inject constructor(
    private val repository: SplitExpenseRepository,
):ApiUseCase<ApiResponseResource<List<GroupExpenseResponseDto>>, GroupRequestDto> {
    override suspend fun execute(args: GroupRequestDto?): ApiResponseResource<List<GroupExpenseResponseDto>> {
        return (args?.let {
            catchWrapper { repository.saveGroup(groupRequestDto = args) }
        } ?: ApiResponseResource.Error("Invalid req.") )
    }
}

class UpdateGroupUseCase @Inject constructor(
    private val repository: SplitExpenseRepository,
): ApiUseCase<ApiResponseResource<Any>, GroupUpdateDeleteRequestPostData> {
    override suspend fun execute(args: GroupUpdateDeleteRequestPostData?): ApiResponseResource<Any> {
        return (args?.let {
            args.id?.let { groupId ->
                args.groupRequestData?.let { groupRequest ->
                    (groupId to groupRequest)
                }
            }?.let { (groupId, groupRequest) ->
                catchWrapper { repository.updateGroup(
                    groupId = groupId,
                    groupRequestDto = groupRequest) }
            } ?: ApiResponseResource.Error("Invalid req.")

        } ?: ApiResponseResource.Error("Invalid req.") )
    }
}

class DeleteGroupUseCase @Inject constructor(
    private val repository: SplitExpenseRepository,
): ApiUseCase<ApiResponseResource<Any>, Int> {
    override suspend fun execute(args: Int?): ApiResponseResource<Any> {
        return (args?.let {
            catchWrapper { repository.deleteGroup(
                groupId = args) }
        } ?: ApiResponseResource.Error("Invalid req.") )
    }
}