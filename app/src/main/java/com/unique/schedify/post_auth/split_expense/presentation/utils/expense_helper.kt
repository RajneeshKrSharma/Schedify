package com.unique.schedify.post_auth.split_expense.presentation.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.unique.schedify.post_auth.post_auth_utils.ExpenseType

@Composable
fun getColorForExpenseType(expenseType: ExpenseType): Color {
    return when(expenseType) {
        ExpenseType.SELF -> MaterialTheme.colorScheme.surfaceBright
        ExpenseType.SHARED_EQUALLY -> MaterialTheme.colorScheme.onBackground
        ExpenseType.CUSTOM -> MaterialTheme.colorScheme.onPrimaryContainer
    }
}

@Composable
fun getColorForExpenseNomenclature(title: String): Color {
    return when {
        title.contains(ExpenseNomenclature.SELF.description, ignoreCase = true) ->
            MaterialTheme.colorScheme.surfaceBright

        title.contains(ExpenseNomenclature.LEND.description, ignoreCase = true) ->
            MaterialTheme.colorScheme.primary

        title.contains(ExpenseNomenclature.OWE.description, ignoreCase = true) ->
            MaterialTheme.colorScheme.secondary

        else -> MaterialTheme.colorScheme.outline
    }
}
