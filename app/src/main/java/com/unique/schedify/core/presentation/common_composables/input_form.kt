package com.unique.schedify.core.presentation.common_composables

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.unique.schedify.R
import com.unique.schedify.core.presentation.utils.FormFieldType
import com.unique.schedify.core.presentation.utils.size_units.dp12
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.util.isEmailValid

data class VisibilityCondition(
    val fieldId: String,
    val expectedValue: String
)

data class FormField(
    val id: String,
    val label: String,
    val type: FormFieldType,
    val options: List<String>? = null,
    val value: String = "",
    val isRequired: Boolean = false,
    val visibleIf: VisibilityCondition? = null
)

@Composable
fun RequiredLabel(label: String, isRequired: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label)
        if (isRequired) {
            Text(text = "*", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun getEditTextColors(): TextFieldColors =
    TextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.primary,
        unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
            alpha = 0.5f
        ),
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
            alpha = 0.5f
        ),
        unfocusedContainerColor = MaterialTheme.colorScheme.onTertiaryContainer,
        focusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
            alpha = 0.5f
        ),
        disabledIndicatorColor = MaterialTheme.colorScheme.onTertiary,
        errorIndicatorColor = MaterialTheme.colorScheme.secondary,
        cursorColor = MaterialTheme.colorScheme.primary
    )

@Composable
fun FormBuilder(
    fields: List<FormField>,
    onFormChanged: (Map<String, String>) -> Unit,
    onDone: () -> Unit,
) {
    val formState = remember { mutableStateMapOf<String, String>() }

    fields.forEach { field ->
        if (formState[field.id] == null) {
            formState[field.id] = field.value
        }
    }

    fun updateField(id: String, value: String) {
        formState[id] = value
        onFormChanged(formState.toMap())
    }

    Column {
        fields.forEachIndexed { index, field ->
            val isVisible = field.visibleIf?.let {
                formState[it.fieldId]?.split(",")?.contains(it.expectedValue) == true
            } ?: true

            if (!isVisible) return@forEachIndexed

            val isActuallyRequired = field.isRequired

            val isLastField = fields.indexOfLast { visibleField ->
                visibleField.visibleIf?.let {
                    formState[it.fieldId]?.split(",")?.contains(it.expectedValue) == true
                } ?: true
            } == index

            val imeAction = if (isLastField) ImeAction.Done else ImeAction.Next
            val keyboardActions = KeyboardActions(
                onDone = {
                    if (isLastField) {
                        onDone()
                    }
                }
            )

            Spacer(modifier = Modifier.height(dp12))

            when (field.type) {
                FormFieldType.TEXT, FormFieldType.EMAIL, FormFieldType.NUMBER -> {
                    OutlinedTextField(
                        singleLine = true,
                        minLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = when (field.type) {
                                FormFieldType.EMAIL -> KeyboardType.Email
                                FormFieldType.NUMBER -> KeyboardType.Number
                                else -> KeyboardType.Text
                            },
                            imeAction = imeAction
                        ),
                        keyboardActions = keyboardActions,
                        value = formState[field.id] ?: "",
                        onValueChange = { updateField(field.id, it) },
                        label = { RequiredLabel(field.label, isActuallyRequired) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = getEditTextColors(),
                        supportingText = {
                            when (field.type) {
                                FormFieldType.EMAIL -> {
                                    if (formState[field.id]?.isNotEmpty() == true && formState[field.id]?.isEmailValid() == false) {
                                        Text(
                                            stringResource(R.string.invalid_email_format),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        )
                                    }
                                }
                                FormFieldType.NUMBER -> {}
                                else -> {}
                            }
                        }
                    )
                }

                FormFieldType.CHECKBOX -> {
                    val selectedOptions = remember(formState[field.id]) {
                        (formState[field.id] ?: "")
                            .split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                            .toMutableStateList()
                    }

                    Column {
                        RequiredLabel(field.label, isActuallyRequired)

                        field.options?.forEach { option ->
                            val isChecked = selectedOptions.contains(option)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            if (!selectedOptions.contains(option)) {
                                                selectedOptions.add(option)
                                            }
                                        } else {
                                            selectedOptions.remove(option)
                                        }

                                        // ✅ Always update as comma-separated string in formState
                                        updateField(field.id, selectedOptions.joinToString(","))
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary,
                                        checkmarkColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        uncheckedColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                                Text(option)
                            }
                        }
                    }
                }

                FormFieldType.RADIO -> {
                    Column {
                        RequiredLabel(field.label, isActuallyRequired)
                        field.options?.forEach { option ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = formState[field.id] == option,
                                    onClick = { updateField(field.id, option) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary,
                                        unselectedColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                                Text(option)
                            }
                        }
                    }
                }

                FormFieldType.DROPDOWN -> {
                    var expanded by remember { mutableStateOf(false) }
                    val selectedText = formState[field.id] ?: ""
                    Box {
                        OutlinedTextField(
                            value = selectedText,
                            onValueChange = {},
                            label = { RequiredLabel(field.label, isActuallyRequired) },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            },
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.primary,
                                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                    alpha = 0.5f
                                ),
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                    alpha = 0.5f
                                ),
                                unfocusedContainerColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                focusedContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                    alpha = 0.5f
                                ),
                                disabledIndicatorColor = MaterialTheme.colorScheme.onTertiary,
                                errorIndicatorColor = MaterialTheme.colorScheme.secondary,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )

                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            field.options?.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        updateField(field.id, option)
                                        expanded = false
                                    },
                                    contentPadding = PaddingValues(horizontal = dp16)
                                )
                            }
                        }
                        Spacer(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { expanded = true }
                        )
                    }
                }

                else -> {
                    RequiredLabel("${field.label}: [${field.type.name} Placeholder]", isActuallyRequired)
                }
            }
        }
    }
}
