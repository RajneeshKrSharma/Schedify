package com.unique.schedify.post_auth.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.unique.schedify.core.util.Resource
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.VerifyPinCodeResponseDto
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TakeAddressScreen(
    navController: NavController,
    viewModel: HomeViewmodel,
) {
    var pincode by remember { mutableStateOf("") }
    var selectedAddress by remember {
        mutableStateOf<VerifyPinCodeResponseDto.VerifyPinCodeResponseDtoItem.PostOffice?>(null)
    }
    val coroutineScope = rememberCoroutineScope()
    val pinCodeState = viewModel.pinCodeValidationSuccess.value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 38.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Enter Your Pincode",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )


        OutlinedTextField(
            value = pincode,
            onValueChange = { pincode = it.filter { ch -> ch.isDigit() } }, // Only digits allowed
            label = { Text("Pincode") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                ,
            singleLine = true,
            colors = outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    viewModel.verifyPinCode(pincode)
                    selectedAddress = null // Reset previous selection
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = pincode.length == 6 && pinCodeState !is Resource.Loading,
            shape = RoundedCornerShape(14.dp),

            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        ) {
            if (pinCodeState is Resource.Loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Fetching...",
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                )
            } else {
                Text(
                    "Validate Pincode",
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        if (pinCodeState is Resource.Success) {
            val postOffices = pinCodeState.data?.postOffice

            if (!postOffices.isNullOrEmpty()) {
                Text(
                    "Select Your Area",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                postOffices.forEach { postOffice ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { selectedAddress = postOffice }
                            .background(
                                if (selectedAddress == postOffice) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selectedAddress == postOffice,
                            onClick = { selectedAddress = postOffice },
                            colors = androidx.compose.material3.RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = postOffice?.name ?: "--",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 10.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        println("Selected Address: $selectedAddress")
                        viewModel.saveAddressInfo(selectedAddress)
                        navController.popBackStack()
                    },
                    enabled = selectedAddress != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        "Submit",
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                    )
                }
            } else {
                Text(
                    "No areas found for this pincode.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        if (pinCodeState is Resource.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = pinCodeState.message ?: "Something went wrong",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}