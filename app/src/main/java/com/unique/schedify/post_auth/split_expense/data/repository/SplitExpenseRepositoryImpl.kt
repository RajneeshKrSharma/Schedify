package com.unique.schedify.post_auth.split_expense.data.repository

import com.unique.schedify.post_auth.split_expense.data.remote.dto.CollaboratorRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.CollaboratorResponseDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.SplitExpenseApis
import com.unique.schedify.post_auth.split_expense.domain.repository.SplitExpenseRepository
import retrofit2.Response
import javax.inject.Inject

class SplitExpenseRepositoryImpl @Inject constructor(
    private val splitExpenseApis: SplitExpenseApis
): SplitExpenseRepository {
    override suspend fun getGroupExpense(): Response<GroupExpenseResponseDto> {
        return splitExpenseApis.getGroupExpense()
    }

    override suspend fun saveGroup(groupRequestDto: GroupRequestDto): Response<GroupExpenseResponseDto> {
        return splitExpenseApis.saveGroup(groupRequestDto = groupRequestDto)
    }

    override suspend fun saveCollaborator(collaboratorRequestDto: CollaboratorRequestDto): Response<GroupExpenseResponseDto> {
        return splitExpenseApis.saveCollaborator(collaboratorRequestDto = collaboratorRequestDto)
    }
}