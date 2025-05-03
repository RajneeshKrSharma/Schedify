/*
package com.unique.schedify.post_auth.split_expense.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.google.gson.Gson
import com.unique.schedify.R
import com.unique.schedify.core.presentation.base_composables.BaseCompose
import com.unique.schedify.core.presentation.common_composables.FormBuilder
import com.unique.schedify.core.presentation.common_composables.FormField
import com.unique.schedify.core.presentation.common_composables.GradientButton
import com.unique.schedify.core.presentation.common_composables.LoadingUi
import com.unique.schedify.core.presentation.utils.FormFieldType
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp40
import com.unique.schedify.core.util.Resource
import com.unique.schedify.post_auth.post_auth_utils.AddCollaboratorFields
import com.unique.schedify.post_auth.post_auth_utils.CollaboratorAlterState
import com.unique.schedify.post_auth.post_auth_utils.CollaboratorState
import com.unique.schedify.post_auth.split_expense.data.remote.dto.CollaboratorRequestDto
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import com.unique.schedify.post_auth.split_expense.presentation.utils.prepareCollaboratorRequest
import com.unique.schedify.pre_auth.presentation.Screen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCollaboratorScreen(
    navController: NavController,
    grpItem: GroupExpenseResponseDto,
    viewModel: SplitExpenseViewModel
) {
    val formFields = remember { mutableStateOf(buildFormFields()) }
    val formResultedData = remember { mutableStateOf<Map<String, String>>(mapOf()) }



    val context = LocalContext.current

    LaunchedEffect(key1 =  Unit) {
        viewModel.resetCollaboratorState()
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

                Spacer(modifier = Modifier.height(dp40))

                GradientButton(
                    text = stringResource(R.string.add),
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    enabled = isFormValid,
                    onClick = {
                        prepareCollaboratorRequest(
                            value = formResultedData.value,
                            grpItem = grpItem,
                            alteringState = CollaboratorAlterState.CREATE
                        ).let {
                            request ->
                            if(request != CollaboratorRequestDto.empty()) {
                                viewModel.startCollaboratorChosenProcess(
                                    collaboratorState = CollaboratorState.CREATE,
                                    collaboratorRequestDto = request
                                )
                            }
                        }
                    }
                )
            }

            if( viewModel.collaboratorCreateState.value is Resource.Loading) {
                LoadingUi()
            }

            if(viewModel.collaboratorCreateState.value is Resource.Success &&
                viewModel.getAllGroupDetails.value is Resource.Success) {
                viewModel.resetAddCollaboratorState()
                Toast.makeText(context, "Collaborator added successfully", Toast.LENGTH_SHORT).show()

                navController.popBackStack(
                    route = "${Screen.GroupDetailedScreen.route}/${URLEncoder.encode(Gson().toJson(grpItem), StandardCharsets.UTF_8.toString())}",
                    inclusive = false
                )
            }

            if(viewModel.collaboratorCreateState.value is Resource.Error &&
                viewModel.getAllGroupDetails.value is Resource.Error) {
                viewModel.resetAddCollaboratorState()
                val errorDetails = viewModel.getAllGroupDetails.value
                Toast.makeText(LocalContext.current, errorDetails.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}


private fun buildFormFields(): List<FormField> {
    return listOf(
        FormField(
            id = AddCollaboratorFields.COLLABORATOR_EMAIL_ID.name,
            label = AddCollaboratorFields.COLLABORATOR_EMAIL_ID.description,
            type = FormFieldType.TEXT,
            isRequired = true
        ),
    )
}
*/
