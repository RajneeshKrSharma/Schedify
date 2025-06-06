package com.unique.schedify.post_auth.schedule_list.presentation

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.core.presentation.common_composables.LoadingUi
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import com.unique.schedify.core.util.Resource
import com.unique.schedify.post_auth.schedule_list.data.remote.model.AddScheduleItemRequestModel
import com.unique.schedify.pre_auth.presentation.Screen
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("UnrememberedGetBackStackEntry")
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

    var address by remember { mutableStateOf("") }
    var weatherNotificationEnabled by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.resetAddScheduleListState()
        address = viewModel.getAddressInfo()
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

            ExtendedFloatingActionButton(
                onClick = {

                    val req = AddScheduleItemRequestModel(
                        title = topic,
                        subTitle = description,
                        dateTime = dateTime,
                        location = locationType,
                        files = emptyList(),
                        isWeatherNotifyEnabled = weatherNotificationEnabled,
                    )



                    val files = try {
                        attachedFiles.map { uri ->
                            uriToFile(context, uri)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        emptyList() // return empty list if exception
                    }

                    viewModel.addScheduleItem(req, files)


                },
                icon = {
                    Icon(Icons.Default.Save, contentDescription = "Submit")
               },
                text = {
                    Text("Submit Schedule")
                },
            )
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ){
                    //Icon(Icons.AutoMirrored.Filled.NotListedLocation, contentDescription = "Location")

                    Image(
                        painter = painterResource(id = R.drawable.location_icon),
                        contentDescription = "Location Icon",
                        modifier = Modifier.size(120.dp)
                    )
                    Text(
                        text = address,
                        color = Color.Black,
                        modifier = Modifier.padding(15.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
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
                        .padding(5.dp),
                    shape = RoundedCornerShape(3.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach File",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Attach File (${attachedFiles.size}/5)",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // Weather Notification Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Checkbox(
                        checked = weatherNotificationEnabled,
                        onCheckedChange = { weatherNotificationEnabled = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.onBackground,
                            uncheckedColor = MaterialTheme.colorScheme.onBackground,
                            checkmarkColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),

                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Weather Notification Enable", style = MaterialTheme.typography.bodyLarge)
                }


                // Show Attached Files
                if (attachedFiles.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Attached Files:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)

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
            Navigation.navigateToScreen(
                navigateTo = AvailableScreens.PostAuth.ScheduleListScreen,
                navController = navController,
                popUpToScreen = Screen.SimpleScheduleList
            )
        }
         if (viewModel.addedScheduleItem.value is Resource.Error) {
             Toast.makeText(context, (viewModel.addedScheduleItem.value as Resource.Error).message, Toast.LENGTH_SHORT).show()
         }

        if (viewModel.addedScheduleItem.value is Resource.Loading) {
            LoadingUi()
        }

    }
}


private fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Cannot open input stream for URI: $uri")

    val file = File(context.cacheDir, "${uri.lastPathSegment?.substringAfterLast('/')}" )
    inputStream.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
    return file
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
            },

        ) {
            DatePicker(
                state = datePickerState,
                colors =  DatePickerDefaults.colors(
                            containerColor = Color.White,
                            selectedDayContainerColor = MaterialTheme.colorScheme.onBackground,
                            selectedDayContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            todayDateBorderColor = MaterialTheme.colorScheme.onBackground,
                            weekdayContentColor = MaterialTheme.colorScheme.inversePrimary,
                            dayContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun checkDateTimePickerDialog() {
    DateTimePickerDialog(
        showDatePicker = true,
        onDismiss = {},
        onDateTimeSelected = { _, _ -> }
    )
}

