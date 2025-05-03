package com.unique.schedify.post_auth.split_expense.data.repository

import com.unique.schedify.post_auth.split_expense.data.remote.dto.CollaboratorRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.CollaboratorResponseDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.ExpenseRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto.Collaborator
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.SplitExpenseApis
import com.unique.schedify.post_auth.split_expense.domain.repository.SplitExpenseRepository
import retrofit2.Response
import javax.inject.Inject

class SplitExpenseRepositoryImpl @Inject constructor(
    private val splitExpenseApis: SplitExpenseApis
): SplitExpenseRepository {
    override suspend fun getGroupExpense(): Response<List<GroupExpenseResponseDto>> {
        return splitExpenseApis.getGroupExpense()
    }

    override suspend fun saveGroup(groupRequestDto: GroupRequestDto): Response<List<GroupExpenseResponseDto>> {
        return splitExpenseApis.saveGroup(groupRequestDto = groupRequestDto)
    }

    override suspend fun saveCollaborator(collaboratorRequestDto: CollaboratorRequestDto): Response<Collaborator> {
        return splitExpenseApis.saveCollaborator(collaboratorRequestDto = collaboratorRequestDto)
    }

    override suspend fun saveExpense(expenseRequestDto: ExpenseRequestDto): Response<Any> {
        return splitExpenseApis.saveExpense(expenseRequestDto = expenseRequestDto)
    }

    override suspend fun updateCollaborator(collaboratorRequestDto: CollaboratorRequestDto): Response<Collaborator> {
        return splitExpenseApis.updateCollaborator(
            collaboratorId = collaboratorRequestDto.collaboratorId ?: -1,
            collaboratorRequestDto = collaboratorRequestDto)
    }

    override suspend fun deleteCollaborator(collaboratorRequestDto: CollaboratorRequestDto): Response<Collaborator> {
        return splitExpenseApis.deleteCollaborator(collaboratorId = collaboratorRequestDto.collaboratorId ?: -1)
    }

    override suspend fun updateGroup(
        groupId: Int,
        groupRequestDto: GroupRequestDto
    ): Response<Any> {
        return splitExpenseApis.updateGroup(
            groupId = groupId,
            groupRequestDto = groupRequestDto
        )
    }

    override suspend fun deleteGroup(groupId: Int): Response<Any> {
        return splitExpenseApis.deleteGroup(groupId = groupId)
    }

    override suspend fun updateExpense(
        expenseId: Int,
        expenseRequestDto: ExpenseRequestDto
    ): Response<Any> {
        return splitExpenseApis.updateExpense(
            expenseId = expenseId,
            expenseRequestDto = expenseRequestDto
        )
    }

    override suspend fun deleteExpense(expenseId: Int): Response<Any> {
        return splitExpenseApis.deleteExpense(
            expenseId = expenseId
        )
    }
}