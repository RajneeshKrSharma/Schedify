package com.unique.schedify.post_auth.split_expense.data.remote.other

import com.unique.schedify.post_auth.post_auth_utils.CollaboratorState
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto

data class UpdateDeleteInfoModel(
    val perform: Any,
    val collaborator: GroupExpenseResponseDto.Collaborator? = null,
    val groupExpenseResponseDto: GroupExpenseResponseDto? = null,
    val expenseResponseDto: GroupExpenseResponseDto.Collaborator.AllExpenses.Expense? = null
) {
    companion object {
        fun empty() = UpdateDeleteInfoModel(
            perform = CollaboratorState.DEFAULT,
        )
    }
}
