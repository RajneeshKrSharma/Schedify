package com.unique.schedify.post_auth.split_expense.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.core.presentation.base_composables.BaseCompose
import com.unique.schedify.core.presentation.common_composables.FormBuilder
import com.unique.schedify.core.presentation.common_composables.FormField
import com.unique.schedify.core.presentation.common_composables.GradientButton
import com.unique.schedify.core.presentation.common_composables.VisibilityCondition
import com.unique.schedify.core.presentation.utils.FormFieldType
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.post_auth.post_auth_utils.AddCollaboratorFields
import com.unique.schedify.post_auth.post_auth_utils.OFFLINE
import com.unique.schedify.post_auth.post_auth_utils.ONLINE
import com.unique.schedify.post_auth.post_auth_utils.OfflinePaymentsOptions
import com.unique.schedify.post_auth.post_auth_utils.OnlinePaymentsOptions
import com.unique.schedify.post_auth.post_auth_utils.SETTLE_MEDIUM_OFFLINE
import com.unique.schedify.post_auth.post_auth_utils.SETTLE_MEDIUM_ONLINE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCollaboratorScreen(
    navController: NavController,
    viewModel: SplitExpenseViewModel
) {
    val formFields = remember { mutableStateOf(buildFormFields()) }
    val formResultedData = remember { mutableStateOf<Map<String, String>>(mapOf()) }

    val isFormValid = formFields.value.all { field ->
        val value = formResultedData.value[field.id]
        val isVisible = field.visibleIf?.let {
            formResultedData.value[it.fieldId]?.split(",")?.contains(it.expectedValue) == true
        } ?: true

        !isVisible || !field.isRequired || value?.isNotEmpty() == true
    }

    BaseCompose(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.add_collaborator),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                modifier = Modifier.padding(dp16)
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(dp16)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FormBuilder(fields = formFields.value) { formData ->
                    println("Form Data = $formData")
                    formResultedData.value = formData
                }

                Spacer(modifier = Modifier.height(dp16))

                GradientButton(
                    text = stringResource(R.string.add),
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    enabled = isFormValid,
                    onClick = {
                        // TODO
                        formFields.value.forEach {
                            println("${it.label} = ${it.value}")
                        }
                    }
                )
            }
        }
    }
}


private fun buildFormFields(): List<FormField> {
    return listOf(
        FormField(
            id = AddCollaboratorFields.COLLABORATOR_NAME.name,
            label = AddCollaboratorFields.COLLABORATOR_NAME.description,
            type = FormFieldType.TEXT,
            isRequired = true
        ),
        FormField(
            id = AddCollaboratorFields.PAYMENT_QR_URL.name,
            label = AddCollaboratorFields.PAYMENT_QR_URL.description,
            type = FormFieldType.TEXT,
            visibleIf = VisibilityCondition(SETTLE_MEDIUM_ONLINE, OnlinePaymentsOptions.UPI.description),
            isRequired = true
        ),
        FormField(
            id = AddCollaboratorFields.REDIRECT_UPI_URL.name,
            label = AddCollaboratorFields.REDIRECT_UPI_URL.description,
            type = FormFieldType.TEXT,
            visibleIf = VisibilityCondition(SETTLE_MEDIUM_ONLINE, OnlinePaymentsOptions.UPI.description),
            isRequired = true
        ),
        FormField(
            id = AddCollaboratorFields.PREFERRED_SETTLE_MODE.name,
            label = AddCollaboratorFields.PREFERRED_SETTLE_MODE.description,
            type = FormFieldType.CHECKBOX,
            options = listOf(OFFLINE, ONLINE),
            isRequired = true
        ),
        FormField(
            id = SETTLE_MEDIUM_ONLINE,
            label = AddCollaboratorFields.PREFERRED_SETTLE_MEDIUM.description,
            type = FormFieldType.CHECKBOX,
            visibleIf = VisibilityCondition(AddCollaboratorFields.PREFERRED_SETTLE_MODE.name, ONLINE),
            options = listOf(
                OnlinePaymentsOptions.UPI.description,
                OnlinePaymentsOptions.NET_BANKING.description,
                OnlinePaymentsOptions.CREDIT_CARD.description,
                OnlinePaymentsOptions.DEBIT_CARD.description,
                OnlinePaymentsOptions.NEFT.description,
                OnlinePaymentsOptions.RTGS.description,
                OnlinePaymentsOptions.DRAFT.description,
            ),
            isRequired = true
        ),
        FormField(
            id = SETTLE_MEDIUM_OFFLINE,
            label = AddCollaboratorFields.PREFERRED_SETTLE_MEDIUM.description,
            type = FormFieldType.CHECKBOX,
            visibleIf = VisibilityCondition(AddCollaboratorFields.PREFERRED_SETTLE_MODE.name, OFFLINE),
            options = listOf(OfflinePaymentsOptions.CASH.description, OfflinePaymentsOptions.BARTER.description),
            isRequired = true
        ),
    )
}
