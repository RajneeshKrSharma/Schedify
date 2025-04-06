package com.unique.schedify.post_auth.split_expense.domain.repository

import com.unique.schedify.post_auth.split_expense.data.remote.dto.CollaboratorRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.CollaboratorResponseDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupRequestDto
import retrofit2.Response

interface SplitExpenseRepository {
    suspend fun getGroupExpense(): Response<GroupExpenseResponseDto>

    suspend fun saveGroup(groupRequestDto: GroupRequestDto): Response<GroupExpenseResponseDto>

    suspend fun saveCollaborator(collaboratorRequestDto: CollaboratorRequestDto): Response<GroupExpenseResponseDto>
}