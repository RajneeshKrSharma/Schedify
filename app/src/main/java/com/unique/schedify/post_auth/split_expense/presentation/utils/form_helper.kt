package com.unique.schedify.post_auth.split_expense.presentation.utils

import android.util.Log
import androidx.compose.runtime.MutableState
import com.unique.schedify.core.presentation.common_composables.FormField
import com.unique.schedify.core.presentation.common_composables.VisibilityCondition
import com.unique.schedify.core.presentation.utils.FormFieldType
import com.unique.schedify.post_auth.post_auth_utils.AddCollaboratorFields
import com.unique.schedify.post_auth.post_auth_utils.AddExpenseFields
import com.unique.schedify.post_auth.post_auth_utils.AddGroupFields
import com.unique.schedify.post_auth.post_auth_utils.EditCollaboratorFields
import com.unique.schedify.post_auth.post_auth_utils.ExpenseQuantityUnitsOptions
import com.unique.schedify.post_auth.post_auth_utils.ExpenseType
import com.unique.schedify.post_auth.post_auth_utils.OFFLINE
import com.unique.schedify.post_auth.post_auth_utils.ONLINE
import com.unique.schedify.post_auth.post_auth_utils.OfflinePaymentsOptions
import com.unique.schedify.post_auth.post_auth_utils.OnlinePaymentsOptions
import com.unique.schedify.post_auth.post_auth_utils.SETTLE_MEDIUM_OFFLINE
import com.unique.schedify.post_auth.post_auth_utils.SETTLE_MEDIUM_ONLINE
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto

fun isFormValid(
    formFields: MutableState<List<FormField>>,
    formResultedData: MutableState<Map<String, String>>
): Boolean {
    val isFormValid = formFields.value.all { field ->
        val value = formResultedData.value[field.id]
        val isVisible = field.visibleIf?.let {
            formResultedData.value[it.fieldId]?.split(",")?.contains(it.expectedValue) == true
        } ?: true

        !isVisible || !field.isRequired || value?.isNotEmpty() == true
    }

    return isFormValid
}

fun buildAddGroupFormFields(): List<FormField> {
    return listOf(
        FormField(
            id = AddGroupFields.GROUP_NAME.name,
            label = AddGroupFields.GROUP_NAME.description,
            type = FormFieldType.TEXT,
            isRequired = true
        ),
    )
}

fun buildUpdateGroupFormFields(
    groupExpenseResponseDto: GroupExpenseResponseDto?
): List<FormField> {
    return listOf(
        FormField(
            id = AddGroupFields.GROUP_NAME.name,
            label = AddGroupFields.GROUP_NAME.description,
            value = groupExpenseResponseDto?.name ?: "RJ",
            type = FormFieldType.TEXT,
            isRequired = true
        ),
    )
}

fun buildAddCollaboratorFormFields(): List<FormField> {
    return listOf(
        FormField(
            id = AddCollaboratorFields.COLLABORATOR_EMAIL_ID.name,
            label = AddCollaboratorFields.COLLABORATOR_EMAIL_ID.description,
            type = FormFieldType.EMAIL,
            isRequired = true
        ),
    )
}

fun buildEditCollaboratorFormFields(
    collaborator: GroupExpenseResponseDto.Collaborator
): List<FormField> {
    return listOf(
        FormField(
            id = EditCollaboratorFields.COLLABORATOR_NAME.name,
            label = EditCollaboratorFields.COLLABORATOR_NAME.description,
            type = FormFieldType.TEXT,
            value = collaborator.collaboratorName ?: "",
            isRequired = true
        ),
        FormField(
            id = EditCollaboratorFields.PREFERRED_SETTLE_MODE.name,
            label = EditCollaboratorFields.PREFERRED_SETTLE_MODE.description,
            type = FormFieldType.CHECKBOX,
            options = listOf(OFFLINE, ONLINE),
            isRequired = true
        ),
        FormField(
            id = SETTLE_MEDIUM_ONLINE,
            label = EditCollaboratorFields.PREFERRED_SETTLE_MEDIUM.description,
            type = FormFieldType.CHECKBOX,
            visibleIf = VisibilityCondition(EditCollaboratorFields.PREFERRED_SETTLE_MODE.name, ONLINE),
            options = listOf(
                OnlinePaymentsOptions.UPI.description,
                OnlinePaymentsOptions.NET_BANKING.description,
                OnlinePaymentsOptions.NEFT.description,
            ),
            isRequired = true
        ),
        FormField(
            id = SETTLE_MEDIUM_OFFLINE,
            label = EditCollaboratorFields.PREFERRED_SETTLE_MEDIUM.description,
            type = FormFieldType.CHECKBOX,
            visibleIf = VisibilityCondition(EditCollaboratorFields.PREFERRED_SETTLE_MODE.name, OFFLINE),
            options = listOf(OfflinePaymentsOptions.CASH.description, OfflinePaymentsOptions.BARTER.description),
            isRequired = true
        ),
    )
}

fun buildAddExpenseFormFields(
    grpItem: GroupExpenseResponseDto
): List<FormField> {

    val preFields = listOf(
        FormField(
            id = AddExpenseFields.ITEM_NAME.name,
            label = AddExpenseFields.ITEM_NAME.description,
            type = FormFieldType.TEXT,
            isRequired = true
        ),
        FormField(
            id = AddExpenseFields.ITEM_QUANTITY.name,
            label = AddExpenseFields.ITEM_QUANTITY.description,
            type = FormFieldType.TEXT,
            isRequired = true
        ),
        FormField(
            id = AddExpenseFields.ITEM_QUANTITY_UNIT.name,
            label = AddExpenseFields.ITEM_QUANTITY_UNIT.description,
            type = FormFieldType.DROPDOWN,
            options = ExpenseQuantityUnitsOptions.unitOptions(),
            isRequired = true
        ),
        FormField(
            id = AddExpenseFields.ITEM_CUSTOM_UNIT.name,
            label = AddExpenseFields.ITEM_CUSTOM_UNIT.description,
            type = FormFieldType.TEXT,
            visibleIf = VisibilityCondition(AddExpenseFields.ITEM_QUANTITY_UNIT.name, ExpenseQuantityUnitsOptions.Other.name),
            isRequired = true
        ),
        FormField(
            id = AddExpenseFields.ITEM_AMOUNT.name,
            label = AddExpenseFields.ITEM_AMOUNT.description,
            type = FormFieldType.NUMBER,
            isRequired = true
        ),
        FormField(
            id = AddExpenseFields.ITEM_DESCRIPTION.name,
            label = AddExpenseFields.ITEM_DESCRIPTION.description,
            type = FormFieldType.TEXT,
            isRequired = false
        ),
        FormField(
            id = AddExpenseFields.EXPENSE_TYPE.name,
            label = AddExpenseFields.EXPENSE_TYPE.description,
            type = FormFieldType.RADIO,
            options = buildList {
                add(ExpenseType.SELF.description)
                if ((grpItem.collaborators?.size ?: 0) > 1) {
                    add(ExpenseType.SHARED_EQUALLY.description)
                    add(ExpenseType.CUSTOM.description)
                }
            },
            isRequired = true
        ),
    )

    val relatedCollaborator = grpItem.collaborators?.map { collab ->
        FormField(
            id = "i_collaborator_${collab?.id ?: 0}",
            label = "${AddExpenseFields.COLLABORATOR_CUSTOM_SHARE.description} ${collab?.collaboratorName ?: "N/A"}",
            type = FormFieldType.NUMBER,
            visibleIf = VisibilityCondition(
                AddExpenseFields.EXPENSE_TYPE.name,
                ExpenseType.CUSTOM.description
            ),
            isRequired = true
        )
    } ?: emptyList()

    return preFields + relatedCollaborator
}
