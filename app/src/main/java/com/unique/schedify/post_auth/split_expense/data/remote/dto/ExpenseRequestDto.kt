package com.unique.schedify.post_auth.split_expense.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ExpenseRequestDto(
    @SerializedName("eName")
    val itemName: String,

    @SerializedName("eQty")
    val itemQuantity: Int,

    @SerializedName("eQtyUnit")
    val itemQuantityUnit: String,

    @SerializedName("eRawAmt")
    val itemAmount: Double,

    @SerializedName("eDescription")
    val itemDescription: String,

    @SerializedName("eExpenseType")
    val eExpenseType: String,

    @SerializedName("addedByCollaboratorId")
    val addedByCollaboratorId: Int,

    @SerializedName("groupId")
    val groupId: Int,
) {
    companion object {
        fun default() : ExpenseRequestDto = ExpenseRequestDto(
            itemName = "",
            itemQuantity = 0,
            itemQuantityUnit = "",
            itemAmount = 0.0,
            itemDescription = "",
            eExpenseType = "",
            addedByCollaboratorId = 0,
            groupId = 0
        )
    }
}

data class ExpenseUpdateDeleteRequestPostData(
    val expenseCreationId: String? = null,
    val expenseRequestData: ExpenseRequestDto? = null
)
