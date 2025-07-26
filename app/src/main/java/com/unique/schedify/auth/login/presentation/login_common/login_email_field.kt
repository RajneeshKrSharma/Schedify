package com.unique.schedify.auth.login.presentation.login_common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.unique.schedify.R
import com.unique.schedify.auth.login.presentation.LoginViewmodel
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.size_units.sp16

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.unique.schedify.core.presentation.common_composables.getEditTextColors
import com.unique.schedify.core.presentation.utils.isEmailValid

@Composable
fun LoginInputField(
    modifier: Modifier,
    enabled: Boolean,
    loginViewmodel: LoginViewmodel,
    onEmailValidDoneCallback: (() -> Unit)? = null
) {
    val emailIdValue by rememberUpdatedState(newValue = loginViewmodel.email.value)

    val focusRequester = remember { FocusRequester() }

    // ✅ Request focus when field is enabled
    LaunchedEffect(enabled) {
        if (enabled) {
            focusRequester.requestFocus()
        }
    }

    OutlinedTextField(
        value = emailIdValue,
        onValueChange = { newValue ->
            loginViewmodel.email.value = newValue
        },
        label = { Text(stringResource(R.string.email_id)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = if (emailIdValue.isEmailValid()) ImeAction.Done else ImeAction.None,
            autoCorrectEnabled = true
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                if (emailIdValue.isEmailValid()) {
                    onEmailValidDoneCallback?.invoke()
                }
            }
        ),
        modifier = modifier
            .padding(top = dp8)
            .focusRequester(focusRequester),
        enabled = enabled,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontSize = sp16,
            color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary,
        ),
        singleLine = true,
        maxLines = 1,
        supportingText = {
            if (emailIdValue.isNotEmpty() && !emailIdValue.isEmailValid()) {
                Text(
                    stringResource(R.string.invalid_email_format),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        },
        colors = getEditTextColors(),
    )
}
