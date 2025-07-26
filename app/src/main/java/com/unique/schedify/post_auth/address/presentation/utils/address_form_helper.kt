package com.unique.schedify.post_auth.address.presentation.utils

import com.unique.schedify.core.presentation.common_composables.FormField
import com.unique.schedify.core.presentation.utils.FormFieldErrorForId
import com.unique.schedify.core.presentation.utils.FormFieldType

fun buildPincodeFormFields(
    pincodeValue: String?
): List<FormField> {
    return listOf(
        FormField(
            value = pincodeValue ?: "",
            id = PincodeFields.PINCODE.name,
            label = PincodeFields.PINCODE.description,
            type = FormFieldType.NUMBER,
            isRequired = true,
            formFieldErrorForId = FormFieldErrorForId.PINCODE
        ),
    )
}