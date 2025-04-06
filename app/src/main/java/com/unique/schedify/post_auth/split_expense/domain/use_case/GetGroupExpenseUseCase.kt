package com.unique.schedify.post_auth.split_expense.domain.use_case

import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import com.unique.schedify.post_auth.split_expense.domain.repository.SplitExpenseRepository
import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.core.util.Resource
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupRequestDto
import javax.inject.Inject

class GetGroupExpenseUseCase @Inject constructor(
    private val repository: SplitExpenseRepository,
    ):ApiUseCase<ApiResponseResource<GroupExpenseResponseDto>, Unit> {
    override suspend fun execute(args: Unit?): ApiResponseResource<GroupExpenseResponseDto> {
        val result = repository.getGroupExpense()
        return (if (result.isSuccessful) {
            result.body()?.let { data ->
                ApiResponseResource.Success(data)
            } ?: ApiResponseResource.Error("")

        } else {
            ApiResponseResource.Error(result.errorBody()?.string() ?: "Something went wrong with error body")
        })
    }
}

class SaveGroupUseCase @Inject constructor(
    private val repository: SplitExpenseRepository,
):ApiUseCase<ApiResponseResource<GroupExpenseResponseDto>, GroupRequestDto> {
    override suspend fun execute(args: GroupRequestDto?): ApiResponseResource<GroupExpenseResponseDto> {
        return (args?.let {
            val result = repository.saveGroup(groupRequestDto = args)
            (if (result.isSuccessful) {
                result.body()?.let { data ->
                    ApiResponseResource.Success(data)
                } ?: ApiResponseResource.Error("")

            } else {
                ApiResponseResource.Error(result.errorBody()?.string() ?: "Something went wrong with error body")
            })

        } ?: ApiResponseResource.Error("Invalid req.") )
    }
}