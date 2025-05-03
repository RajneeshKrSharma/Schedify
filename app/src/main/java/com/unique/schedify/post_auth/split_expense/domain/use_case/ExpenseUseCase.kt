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
            val result = repository.saveExpense(
                expenseRequestDto = args)
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

class UpdateExpenseUseCase @Inject constructor(
    private val repository: SplitExpenseRepository,
): ApiUseCase<ApiResponseResource<Any>, ExpenseUpdateDeleteRequestPostData> {
    override suspend fun execute(args: ExpenseUpdateDeleteRequestPostData?): ApiResponseResource<Any> {
        return (args?.let {

            args.id?.let { expenseId ->
                args.expenseRequestData?.let { expenseRequest ->
                    (expenseId to expenseRequest)
                }
            }?.let { (expenseId, expenseRequest) ->
                val result = repository.updateExpense(
                    expenseId = expenseId,
                    expenseRequestDto = expenseRequest)
                (if (result.isSuccessful) {
                    result.body()?.let { data ->
                        ApiResponseResource.Success(data)
                    } ?: ApiResponseResource.Error("")

                } else {
                    ApiResponseResource.Error(result.errorBody()?.string() ?: "Something went wrong with error body")
                })
            } ?: ApiResponseResource.Error("Invalid req.")

        } ?: ApiResponseResource.Error("Invalid req.") )
    }
}

class DeleteExpenseUseCase @Inject constructor(
    private val repository: SplitExpenseRepository,
): ApiUseCase<ApiResponseResource<Any>, Int> {
    override suspend fun execute(args: Int?): ApiResponseResource<Any> {
        return (args?.let {
            val result = repository.deleteExpense(
                expenseId = args)
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