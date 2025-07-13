package com.unique.schedify.post_auth.split_expense.data.remote.dto

import com.unique.schedify.core.network.Api.COLLABORATOR
import com.unique.schedify.core.network.Api.EXPENSE
import com.unique.schedify.core.network.Api.GROUP_EXPENSE
import com.unique.schedify.core.network.BaseApi
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto.Collaborator
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SplitExpenseApis: BaseApi {
    companion object {
        const val GROUP_ID = "group_id"
        const val EXPENSE_CREATION_ID = "eCreationId"
    }

    @GET(GROUP_EXPENSE)
    suspend fun getGroupExpense(): Response<List<GroupExpenseResponseDto>>

    @POST(GROUP_EXPENSE)
    suspend fun saveGroup(
        @Body groupRequestDto: GroupRequestDto
    ): Response<List<GroupExpenseResponseDto>>

    @POST(COLLABORATOR)
    suspend fun saveCollaborator(
        @Body collaboratorRequestDto: CollaboratorRequestDto
    ): Response<Collaborator>

    @POST(EXPENSE)
    suspend fun saveExpense(
        @Body expenseRequestDto: ExpenseRequestDto
    ): Response<Any>

    @PATCH(COLLABORATOR)
    suspend fun updateCollaborator(
        @Query("id") collaboratorId: Int,
        @Body collaboratorRequestDto: CollaboratorRequestDto
    ): Response<Collaborator>

    @DELETE(COLLABORATOR)
    suspend fun deleteCollaborator(
        @Query("id") collaboratorId: Int,
    ): Response<Collaborator>

    @PATCH(GROUP_EXPENSE)
    suspend fun updateGroup(
        @Query(GROUP_ID) groupId: Int,
        @Body groupRequestDto: GroupRequestDto
    ): Response<Any>

    @DELETE(GROUP_EXPENSE)
    suspend fun deleteGroup(
        @Query(GROUP_ID) groupId: Int,
    ): Response<Any>

    @PATCH(EXPENSE)
    suspend fun updateExpense(
        @Query(EXPENSE_CREATION_ID) expenseCreationId: String,
        @Body expenseRequestDto: ExpenseRequestDto
    ): Response<Any>

    @DELETE(EXPENSE)
    suspend fun deleteExpense(
        @Query(EXPENSE_CREATION_ID) expenseCreationId: String,
    ): Response<Any>
}