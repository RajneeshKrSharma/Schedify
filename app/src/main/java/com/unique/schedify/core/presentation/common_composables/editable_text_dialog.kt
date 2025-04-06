package com.unique.schedify.core.presentation.common_composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.unique.schedify.R

@Composable
fun EditableTextDialog(
    alertDialogTitle: String,
    editFieldText: String,
    onConfirm: (text: String) -> Unit
) {
    var customText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = alertDialogTitle,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = customText,
                    onValueChange = { customText = it },
                    label = {
                        Text(
                            text = editFieldText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(customText) }) {
                Text(
                    stringResource(R.string.ok),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }
        },
    )
}