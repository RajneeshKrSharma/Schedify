package com.unique.schedify.core.presentation.utils

import androidx.compose.runtime.MutableState
import com.unique.schedify.core.presentation.common_composables.FormField
import com.unique.schedify.core.util.EmailIdRegex
import com.unique.schedify.post_auth.address.presentation.utils.PINCODE_LENGTH

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


val emailPattern = EmailIdRegex.toRegex()

fun String.isEmailValid(): Boolean = this.run { (isNotEmpty() && matches(emailPattern)) }

fun String.isPincodeValid(): Boolean = this.run { isNotEmpty() && (length == PINCODE_LENGTH) }
