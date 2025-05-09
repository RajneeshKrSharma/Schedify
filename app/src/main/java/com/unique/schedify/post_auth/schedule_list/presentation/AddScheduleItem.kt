package com.unique.schedify.post_auth.schedule_list.presentation

import android.app.TimePickerDialog
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.unique.schedify.core.util.Resource
import com.unique.schedify.post_auth.schedule_list.data.remote.model.AddScheduleItemRequestModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleScreen(
    navController: NavController,
    viewModel: SimpleScheduleListViewModel,
    locationType: String,// "new" or "current"
    location: String? = null
) {
    var pincode by remember { mutableStateOf("") }
    var showFormFields by remember { mutableStateOf((locationType == "current")) }
    var topic by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateTime by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    // File Attachment State
    val attachedFiles = remember { mutableStateListOf<Uri>() }


    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(viewModel.pinCodeValidationSuccess.value) {
        if (viewModel.pinCodeValidationSuccess.value is Resource.Success) {
            viewModel.pinCodeValidationSuccess.value.data?.let { isValid ->
                showFormFields = isValid
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Schedule") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (showFormFields) {
                ExtendedFloatingActionButton(
                    onClick = {
                        val req = AddScheduleItemRequestModel(
                            title = topic,
                            subTitle = description,
                            dateTime = dateTime,
                            location = locationType,
                            files = emptyList()
                        )
                        viewModel.addScheduleItem(req)


                    },
                    icon = { Icon(Icons.Default.Save, "Submit") },
                    text = { Text("Submit Schedule") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (!showFormFields) {
                // Pincode Input Section
                OutlinedTextField(
                    value = pincode,
                    onValueChange = { pincode = it },
                    label = { Text("Pincode") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.verifyPinCode(pincode)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = pincode.length == 6 && (viewModel.pinCodeValidationSuccess.value.data != true),
                    colors = ButtonDefaults.buttonColors(
                       MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    if (viewModel.pinCodeValidationSuccess.value.data == true) CircularProgressIndicator(Modifier.size(24.dp))
                    else Text(
                            if(viewModel.pinCodeValidationSuccess.value is Resource.Loading) "Fetching.." else "Validate Pincode",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                         )
                }
            }
            else {

                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                    uri?.let {
                        if (attachedFiles.size < 5) {
                            attachedFiles.add(it)
                        } else {
                            // Optional: Show a warning using Snackbar or Toast
                            Log.w("FilePicker", "Max 5 files allowed")
                        }
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {

                    // Topic Field
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Topic") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Description Field
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp),
                        maxLines = 4
                    )

                    // Date Picker
                    if (showDatePicker) {
                        DateTimePickerDialog(
                            showDatePicker = showDatePicker,
                            onDismiss = { showDatePicker = false },
                            onDateTimeSelected = { _, formattedDate ->
                                dateTime = formattedDate.toString()
                            }
                        )
                    }

                    OutlinedTextField(
                        value = dateTime,
                        onValueChange = {},
                        label = { Text("Date & Time") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, "Pick Date")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Attach File Button

                    Button(
                        onClick = { launcher.launch("*/*") },
                        enabled = attachedFiles.size < 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        elevation = ButtonDefaults.elevatedButtonElevation(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attach File",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Attach File (${attachedFiles.size}/5)",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }


                    // Show Attached Files
                    if (attachedFiles.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Attached Files:", fontWeight = FontWeight.Bold)

                        attachedFiles.forEachIndexed { index, uri ->
                            AttachmentCard(uri.lastPathSegment ?: "File ${index + 1}")
                            Spacer(modifier = Modifier.height(8.dp))
                            /*Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uri.lastPathSegment ?: "File ${index + 1}",
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                IconButton(onClick = { attachedFiles.remove(uri) }) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove File")
                                }
                            }*/
                        }
                    }
                }
            }

            if (viewModel.pinCodeValidationSuccess.value is Resource.Error) {
                Text(
                    text = (viewModel.pinCodeValidationSuccess.value as Resource.Error).message ?: "Error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )

            }
        }

        if (viewModel.addedScheduleItem.value is Resource.Success) {

            Log.i("TaG", "AddScheduleScreen: -=-=-=-==-=-=-=-=-=->")
            navController.popBackStack()
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    showDatePicker: Boolean,
    onDismiss: () -> Unit,
    onDateTimeSelected: (LocalDateTime, String) -> Unit // updated to return formatted date too
) {
    val context = LocalContext.current
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedDateMillis ->
                            val selectedDate = Instant.ofEpochMilli(selectedDateMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()

                            val calendar = Calendar.getInstance()

                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    val selectedTime = LocalTime.of(hourOfDay, minute)
                                    val dateTime = LocalDateTime.of(selectedDate, selectedTime)

                                    // ✅ Format using java.util.Date
                                    val millis = dateTime
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()
                                        .toEpochMilli()

                                    val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                    val formattedDate = dateFormatter.format(Date(millis))

                                    onDateTimeSelected(dateTime, formattedDate)
                                    onDismiss()
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                            ).show()
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

