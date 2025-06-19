package com.unique.schedify.post_auth.split_expense.domain.repository

import com.unique.schedify.post_auth.split_expense.data.remote.dto.CollaboratorRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.CollaboratorResponseDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.ExpenseRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto.Collaborator
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupRequestDto
import retrofit2.Response

interface SplitExpenseRepository {
    suspend fun getGroupExpense(): Response<List<GroupExpenseResponseDto>>

    suspend fun saveGroup(groupRequestDto: GroupRequestDto): Response<List<GroupExpenseResponseDto>>

    suspend fun updateGroup(groupId: Int, groupRequestDto: GroupRequestDto): Response<Any>

    suspend fun deleteGroup(groupId: Int): Response<Any>

    suspend fun saveCollaborator(collaboratorRequestDto: CollaboratorRequestDto): Response<Collaborator>

    suspend fun updateCollaborator(collaboratorRequestDto: CollaboratorRequestDto): Response<Collaborator>

    suspend fun deleteCollaborator(collaboratorRequestDto: CollaboratorRequestDto): Response<Collaborator>

    suspend fun saveExpense(expenseRequestDto: ExpenseRequestDto): Response<Any>

    suspend fun updateExpense(expenseCreationId: String, expenseRequestDto: ExpenseRequestDto): Response<Any>

    suspend fun deleteExpense(expenseCreationId: String): Response<Any>

}