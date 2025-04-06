package com.unique.schedify.post_auth.split_expense.data.remote.dto

import com.unique.schedify.core.network.Api.COLLABORATOR
import com.unique.schedify.core.network.Api.GROUP_EXPENSE
import com.unique.schedify.core.network.BaseApi
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SplitExpenseApis: BaseApi {
    @GET(GROUP_EXPENSE)
    suspend fun getGroupExpense(): Response<GroupExpenseResponseDto>

    @POST(GROUP_EXPENSE)
    suspend fun saveGroup(
        @Body groupRequestDto: GroupRequestDto
    ): Response<GroupExpenseResponseDto>

    @POST(COLLABORATOR)
    suspend fun saveCollaborator(
        @Body collaboratorRequestDto: CollaboratorRequestDto
    ): Response<GroupExpenseResponseDto>
}