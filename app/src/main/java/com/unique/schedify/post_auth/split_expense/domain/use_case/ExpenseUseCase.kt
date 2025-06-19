package com.unique.schedify.post_auth.split_expense.domain.use_case

import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.post_auth.split_expense.data.remote.dto.ExpenseRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.ExpenseUpdateDeleteRequestPostData
import com.unique.schedify.post_auth.split_expense.domain.repository.SplitExpenseRepository
import javax.inject.Inject

class SaveExpenseUseCase @Inject constructor(
    private val repository: SplitExpenseRepository,
): ApiUseCase<ApiResponseResource<Any>, ExpenseRequestDto> {
    override suspend fun execute(args: ExpenseRequestDto?): ApiResponseResource<Any> {
        return (args?.let {
            catchWrapper {
                repository.saveExpense(
                    expenseRequestDto = args)
            }

        } ?: ApiResponseResource.Error("Invalid req.") )
    }
}

class UpdateExpenseUseCase @Inject constructor(
    private val repository: SplitExpenseRepository,
): ApiUseCase<ApiResponseResource<Any>, ExpenseUpdateDeleteRequestPostData> {
    override suspend fun execute(args: ExpenseUpdateDeleteRequestPostData?): ApiResponseResource<Any> {
        return (args?.let {
            args.expenseCreationId?.let { expenseCreationId ->
                args.expenseRequestData?.let { expenseRequest ->
                    (expenseCreationId to expenseRequest)
                }
            }?.let { (expenseCreationId, expenseRequest) ->
                catchWrapper {
                    repository.updateExpense(
                        expenseCreationId = expenseCreationId,
                        expenseRequestDto = expenseRequest
                    )
                }
            }

        } ?: ApiResponseResource.Error("Invalid req.") )
    }
}

class DeleteExpenseUseCase @Inject constructor(
    private val repository: SplitExpenseRepository,
): ApiUseCase<ApiResponseResource<Any>, String> {
    override suspend fun execute(args: String?): ApiResponseResource<Any> {
        return (args?.let {
            catchWrapper { repository.deleteExpense(
                expenseCreationId = args) }
        } ?: ApiResponseResource.Error("Invalid req.") )
    }
}