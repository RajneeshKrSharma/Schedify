package com.unique.schedify.core.presentation.common_composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.unique.schedify.R
import com.unique.schedify.core.presentation.utils.size_units.dp8

@Composable
fun MultiOptionRadioDialog(
    alertDialogTitle: String,
    options: List<String>,
    selectedOption: String = options.first(),
    onConfirm: (selected: String) -> Unit,
) {
    var selectedText by remember { mutableStateOf(selectedOption) }

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
                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (option == selectedText),
                                onClick = { selectedText = option },
                                role = Role.RadioButton
                            )
                            .padding(vertical = dp8)
                    ) {
                        RadioButton(
                            selected = (option == selectedText),
                            onClick = { selectedText = option }
                        )
                        Spacer(modifier = Modifier.width(dp8))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedText) }) {
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
