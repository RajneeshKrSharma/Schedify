package com.unique.schedify.post_auth.split_expense.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetLayout
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.unique.schedify.core.presentation.common_composables.FormBuilder
import com.unique.schedify.core.presentation.common_composables.FormField
import com.unique.schedify.core.presentation.common_composables.GradientButton
import com.unique.schedify.core.presentation.common_composables.ImageWithLoadingIndicator
import com.unique.schedify.core.presentation.utils.size_units.dp16

@Composable
fun GenericBottomSheet(
    sheetState: ModalBottomSheetState,
    headingTitleText: String,
    buttonText: String,
    formInputDataFields: List<FormField>,
    formOutputData: (resultedData: Map<String, String>) -> Unit,
    dismissSheet: () -> Unit,
    sheetContent: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
    onFormFieldDone: (resultedData: Map<String, String>) -> Unit,
) {

    val resolvedSheetContent: @Composable () -> Unit = sheetContent ?: {
        SheetContent(
            headingTitleText = headingTitleText,
            buttonText = buttonText,
            formInputDataFields = formInputDataFields,
            formOutputData = formOutputData,
            dismissSheet = dismissSheet,
            onFormFieldDone = onFormFieldDone
        )
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {resolvedSheetContent.invoke()},
        sheetBackgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
        sheetShape = RoundedCornerShape(topStart = dp16, dp16),
        scrimColor = MaterialTheme.colorScheme.onTertiary,
        content = {
            content?.invoke()
        }
    )
}

@Composable
private fun SheetContent(
    headingTitleText: String,
    buttonText: String,
    formInputDataFields: List<FormField>,
    formOutputData: (resultedData: Map<String, String>) -> Unit,
    dismissSheet: () -> Unit,
    onFormFieldDone: (resultedData: Map<String, String>) -> Unit,
) {
    val formFields: MutableState<List<FormField>> = remember { mutableStateOf(formInputDataFields) }
    val formResultedData: MutableState<Map<String, String>> = remember { mutableStateOf(mapOf()) }

    Column(
        modifier = Modifier
            .padding(dp16)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = headingTitleText,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            IconButton(onClick = { dismissSheet.invoke() }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Sheet",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(dp16))

        FormBuilder(fields = formFields.value, onFormChanged = { formData ->
            println("Form Data = $formData")
            formResultedData.value = formData
        }, onDone = {
            onFormFieldDone.invoke(formResultedData.value)
            dismissSheet.invoke()
        })

        Spacer(modifier = Modifier.height(dp16))

        GradientButton(
            text = buttonText,
            textStyle = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            enabled = isFormValid(
                formFields = formFields,
                formResultedData = formResultedData
            ),
            onClick = {
                formOutputData.invoke(formResultedData.value)
                dismissSheet.invoke()
            }
        )
    }
}

@Composable
fun EmptyDataUi(
    imageUrl: String,
    msg: String,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(dp16)
            .background(color = MaterialTheme.colorScheme.onSecondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ImageWithLoadingIndicator(
                imageUrl = imageUrl,
                modifier = Modifier.wrapContentSize(),
                imageModifier = modifier
                    .fillMaxWidth()
                    .weight(0.85f),
            )
            Spacer(modifier = Modifier.height(dp16))
            Text(
                msg,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(modifier = Modifier.height(dp16))
            content?.invoke()
        }
    }
}