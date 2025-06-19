package com.unique.schedify.post_auth.split_expense.presentation.utils

enum class ExpenseNomenclature(val description: String) {
    SELF("Self"),
    LEND("Lended"),
    OWE("Owed");

    companion object {
        fun fromDescription(description: String): ExpenseNomenclature {
            return ExpenseNomenclature.entries.find { it.description.equals(description, ignoreCase = true) }
                ?: SELF
        }
    }
}