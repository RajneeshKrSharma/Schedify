package com.unique.schedify.post_auth.split_expense.presentation.utils

import androidx.compose.runtime.MutableState
import com.unique.schedify.core.presentation.common_composables.FormField
import com.unique.schedify.core.presentation.common_composables.VisibilityCondition
import com.unique.schedify.core.presentation.utils.FormFieldErrorForId
import com.unique.schedify.core.presentation.utils.FormFieldType
import com.unique.schedify.post_auth.post_auth_utils.AddCollaboratorFields
import com.unique.schedify.post_auth.post_auth_utils.AddExpenseFields
import com.unique.schedify.post_auth.post_auth_utils.AddGroupFields
import com.unique.schedify.post_auth.post_auth_utils.EditCollaboratorFields
import com.unique.schedify.post_auth.post_auth_utils.ExpenseQuantityUnitsOptions
import com.unique.schedify.post_auth.post_auth_utils.ExpenseType
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto

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
            isRequired = true,
            formFieldErrorForId = FormFieldErrorForId.EMAIL_ID
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
    )
}

fun buildAddExpenseFormFields(
    grpItem: GroupExpenseResponseDto
): List<FormField> {

    val expenseTypeList = buildList {
        add(ExpenseType.SELF.description)

        if ((grpItem.collaborators?.size ?: 0) > 1 &&
            grpItem.collaborators?.all { it?.isActive == true } == true
        ) {
            add(ExpenseType.SHARED_EQUALLY.description)
            // add(ExpenseType.CUSTOM.description)
        }
    }


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
            type = FormFieldType.NUMBER,
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
                if ((grpItem.collaborators?.size ?: 0) > 1
                    && grpItem.collaborators?.all { data -> data?.isActive == true } == true) {
                    add(ExpenseType.SHARED_EQUALLY.description)
                    //add(ExpenseType.CUSTOM.description)
                }
            },
            value = expenseTypeList.takeIf { data -> data.size == 1 }?.let {
                expenseTypeList.first()
            } ?: "",
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


fun buildEditExpenseFormFields(
    grpItem: GroupExpenseResponseDto,
    expense: GroupExpenseResponseDto.Collaborator.AllExpenses.Expense
): List<FormField> {

    val preFields = listOf(
        FormField(
            id = AddExpenseFields.ITEM_NAME.name,
            label = AddExpenseFields.ITEM_NAME.description,
            type = FormFieldType.TEXT,
            value = expense.eName,
        ),
        FormField(
            id = AddExpenseFields.ITEM_QUANTITY.name,
            label = AddExpenseFields.ITEM_QUANTITY.description,
            type = FormFieldType.TEXT,
            value = expense.eQty.toString(),
        ),
        FormField(
            id = AddExpenseFields.ITEM_QUANTITY_UNIT.name,
            label = AddExpenseFields.ITEM_QUANTITY_UNIT.description,
            type = FormFieldType.DROPDOWN,
            options = ExpenseQuantityUnitsOptions.unitOptions(),
            value = expense.eQtyUnit,
        ),
        FormField(
            id = AddExpenseFields.ITEM_CUSTOM_UNIT.name,
            label = AddExpenseFields.ITEM_CUSTOM_UNIT.description,
            type = FormFieldType.TEXT,
            visibleIf = VisibilityCondition(AddExpenseFields.ITEM_QUANTITY_UNIT.name, ExpenseQuantityUnitsOptions.Other.name),
            value = expense.eQtyUnit,
        ),
        FormField(
            id = AddExpenseFields.ITEM_AMOUNT.name,
            label = AddExpenseFields.ITEM_AMOUNT.description,
            type = FormFieldType.NUMBER,
            value = expense.eRawAmt.toString(),
        ),
        FormField(
            id = AddExpenseFields.ITEM_DESCRIPTION.name,
            label = AddExpenseFields.ITEM_DESCRIPTION.description,
            type = FormFieldType.TEXT,
        ),
        FormField(
            id = AddExpenseFields.EXPENSE_TYPE.name,
            label = AddExpenseFields.EXPENSE_TYPE.description,
            type = FormFieldType.RADIO,
            options = buildList {
                add(ExpenseType.SELF.description)
                if ((grpItem.collaborators?.size ?: 0) > 1
                    && grpItem.collaborators?.all { data -> data?.isActive == true } == true
                ) {
                    add(ExpenseType.SHARED_EQUALLY.description)
                    //add(ExpenseType.CUSTOM.description)
                }
            },
        ),
    )

    return preFields;
}