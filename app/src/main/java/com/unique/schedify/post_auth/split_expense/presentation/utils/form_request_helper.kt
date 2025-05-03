package com.unique.schedify.post_auth.split_expense.presentation.utils

import com.unique.schedify.post_auth.post_auth_utils.AddCollaboratorFields
import com.unique.schedify.post_auth.post_auth_utils.AddExpenseFields
import com.unique.schedify.post_auth.post_auth_utils.AddGroupFields
import com.unique.schedify.post_auth.post_auth_utils.CollaboratorAlterState
import com.unique.schedify.post_auth.post_auth_utils.EditCollaboratorFields
import com.unique.schedify.post_auth.post_auth_utils.ExpenseAlterState
import com.unique.schedify.post_auth.post_auth_utils.ExpenseQuantityUnitsOptions
import com.unique.schedify.post_auth.post_auth_utils.GroupAlterState
import com.unique.schedify.post_auth.post_auth_utils.OnlinePaymentsOptions
import com.unique.schedify.post_auth.post_auth_utils.SETTLE_MEDIUM_OFFLINE
import com.unique.schedify.post_auth.post_auth_utils.SETTLE_MEDIUM_ONLINE
import com.unique.schedify.post_auth.split_expense.data.remote.dto.CollaboratorRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.ExpenseRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupRequestDto

fun prepareGroupRequest(
    value: Map<String, String>,
    alteringState: GroupAlterState
): GroupRequestDto {
    when(alteringState) {
        GroupAlterState.CREATE -> {
            val gName = value[AddGroupFields.GROUP_NAME.name].orEmpty()
            return GroupRequestDto.empty().copy(
                grpName = gName
            )
        }

        GroupAlterState.UPDATE -> {
            val gName = value[AddGroupFields.GROUP_NAME.name].orEmpty()
            return GroupRequestDto.empty().copy(
                grpName = gName
            )
        }
    }
}

fun prepareExpenseRequest(
    value: Map<String, String>,
    alteringState: ExpenseAlterState
): ExpenseRequestDto {

    when(alteringState) {
        ExpenseAlterState.CREATE -> {
            val itemName = value[AddExpenseFields.ITEM_NAME.name].orEmpty()
            val itemQuantity = value[AddExpenseFields.ITEM_QUANTITY.name]?.toIntOrNull() ?: 0
            val itemQuantityUnit = value[AddExpenseFields.ITEM_QUANTITY_UNIT.name].orEmpty()
            val itemAmount = value[AddExpenseFields.ITEM_AMOUNT.name]?.toDoubleOrNull() ?: 0.0
            val itemDescription = value[AddExpenseFields.ITEM_DESCRIPTION.name].orEmpty()
            val eExpenseType = value[AddExpenseFields.EXPENSE_TYPE.name].orEmpty()
            val eCustomUnit = value[AddExpenseFields.ITEM_CUSTOM_UNIT.name].orEmpty()

            return ExpenseRequestDto.default().copy(
                itemName = itemName,
                itemQuantity = itemQuantity,
                itemQuantityUnit = itemQuantityUnit.takeIf { it != ExpenseQuantityUnitsOptions.Other.name } ?: eCustomUnit,
                itemAmount = itemAmount,
                itemDescription = itemDescription,
                eExpenseType = eExpenseType
            )
        }

        ExpenseAlterState.UPDATE -> TODO()
    }
}

fun prepareCollaboratorRequest(
    collaborator: GroupExpenseResponseDto.Collaborator? = null,
    value: Map<String, String>,
    grpItem: GroupExpenseResponseDto,
    alteringState: CollaboratorAlterState

): CollaboratorRequestDto {
    when(alteringState) {
        CollaboratorAlterState.CREATE -> {
            val collaboratorEmailId = value[AddCollaboratorFields.COLLABORATOR_EMAIL_ID.name].orEmpty()

            return grpItem.id?.let { groupId ->
                CollaboratorRequestDto.empty().copy(
                    groupId = groupId,
                    emailId = collaboratorEmailId
                )
            } ?: CollaboratorRequestDto.empty()
        }

        CollaboratorAlterState.UPDATE -> {
            val collaboratorName = value[EditCollaboratorFields.COLLABORATOR_NAME.name].orEmpty()
            val settleModes = value[EditCollaboratorFields.PREFERRED_SETTLE_MODE.name]?.split(",") ?: listOf()
            val settleOnlineMediums = value[SETTLE_MEDIUM_ONLINE]?.split(",") ?: listOf()
            val settleOfflineMediums = value[SETTLE_MEDIUM_OFFLINE]?.split(",") ?: listOf()
            val paymentQrUrl = value[EditCollaboratorFields.PAYMENT_QR_URL.name].orEmpty()
            val redirectUpiUrl = value[EditCollaboratorFields.REDIRECT_UPI_URL.name].orEmpty()

            return grpItem.id?.let { groupId ->
                CollaboratorRequestDto.empty().copy(
                    collaboratorId = collaborator?.id,
                    groupId = groupId,
                    collaboratorName = collaboratorName,
                    settleModes = settleModes,
                    settleMediums = settleOnlineMediums + settleOfflineMediums,
                    requestedPaymentQrUrl = if(settleOnlineMediums.contains(OnlinePaymentsOptions.UPI.description)) paymentQrUrl else null,
                    redirectUpiUrl = if(settleOnlineMediums.contains(OnlinePaymentsOptions.UPI.description)) redirectUpiUrl else null
                )
            } ?: CollaboratorRequestDto.empty()
        }
    }
}