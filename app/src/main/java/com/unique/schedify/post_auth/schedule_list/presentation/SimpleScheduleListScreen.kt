package com.unique.schedify.post_auth.schedule_list.presentation

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.gson.Gson
import com.unique.schedify.core.presentation.utils.size_units.dp10
import com.unique.schedify.core.presentation.utils.size_units.dp12
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp4
import com.unique.schedify.core.presentation.utils.size_units.dp72
import com.unique.schedify.core.util.Resource
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.ScheduleListResponseDto
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*// Sample Data Class
data class ScheduleItem(
    val id: Int,
    val title: String,
    val subTitle: String,
    val dateTime: String,
    val lastScheduleOn: String,
    val priority: Int,
    val isItemPinned: Boolean,
    val isArchived: Boolean,
    val attachments: List<String>
)*/


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleScheduleListScreen(
    navController: NavController,
    viewModel: SimpleScheduleListViewModel
) {
    val scheduleItems = viewModel.scheduleItems.value
    var fabExpanded by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnResume by rememberUpdatedState(newValue = {
        // This is called when Screen A resumes

    })

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                currentOnResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Add Schedule with New Location FAB
                /*AnimatedVisibility(
                    visible = fabExpanded,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it }
                ) {
                    FloatingActionButton(
                        onClick = {
                            fabExpanded = false
                            navController.navigate("add_schedule?locationType=new") //new
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(Icons.Default.LocationOn, "New Location")
                    }
                }*/

                // Add Schedule with Current Location FAB
                AnimatedVisibility(
                    visible = fabExpanded,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it }
                ) {
                    FloatingActionButton(
                        onClick = {
                            fabExpanded = false
                            navController.navigate("add_schedule?locationType=current")
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(Icons.Default.MyLocation, "Current Location")
                    }
                }

                // Main FAB
                FloatingActionButton(
                    onClick = { fabExpanded = !fabExpanded }
                ) {
                    Icon(
                        if (fabExpanded) Icons.Default.Close else Icons.Default.Add,
                        "Add Schedule"
                    )
                }
            }
        }
    ) { padding ->


        // Content List
        if (viewModel.scheduleItems.value is Resource.Success) {
            if (scheduleItems.data != null && scheduleItems.data.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .padding(dp10),
                    verticalArrangement = Arrangement.spacedBy(dp12)
                ) {
                    items(scheduleItems.data, key = { it?.id!! }) { item ->
                        ScheduleListItem(
                            item = item,
                            onClick = {
                                val json = Uri.encode(Gson().toJson(item))
                                navController.navigate("schedule_detail/$json")

                            }
                        )
                    }
                }
            }
            else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dp16),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No schedules available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
        else if (viewModel.scheduleItems.value is Resource.Loading){
            Box(modifier = Modifier
                .padding(padding)
                .padding(dp10)
                .fillMaxWidth(),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
        else if (viewModel.scheduleItems.value is Resource.Error) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dp16),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Failed to load schedules",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

    }

}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScheduleListItem(
    item: ScheduleListResponseDto.Data?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Hero animation modifier
    val heroModifier = Modifier
        .graphicsLayer {
            // Shared element transition setup
            clip = false
            shape = RoundedCornerShape(24.dp)
            shadowElevation = if (item?.isItemPinned == true) 12.dp.toPx() else 8.dp.toPx()
        }
        .border(
            width = 1.dp,
            color = when (item?.priority) {
                2 -> Color.Red.copy(alpha = 0.3f)
                1 -> Color.Yellow.copy(alpha = 0.3f)
                else -> Color.Blue.copy(alpha = 0.1f)
            },
            shape = RoundedCornerShape(24.dp)
        )

    // Time bubble shape with dynamic size based on time proximity
    val timeProximity = calculateTimeProximity(item?.dateTime ?: "01-01-2025 00:00:00 AM")
    val bubbleSize by animateDpAsState(
        targetValue = (48 + timeProximity * 24).dp,
        animationSpec = spring(dampingRatio = 0.6f)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = dp72)
            .then(heroModifier)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dp12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time Bubble (Hero element)
            Box(
                modifier = Modifier
                    .size(bubbleSize)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                when (item?.priority) {
                                    2 -> Color(0xFFFF6B6B)
                                    1 -> Color(0xFFFFD166)
                                    else -> Color(0xFF4ECDC4)
                                },
                                when (item?.priority) {
                                    2 -> Color(0xFFEE5253)
                                    1 -> Color(0xFFFF9F43)
                                    else -> Color(0xFF1DD1A1)
                                }
                            )
                        ),
                        shape = CircleShape
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item?.dateTime?.split(" ")[1]!!.substring(0, 5),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                    Text(
                        text = item.dateTime.split("-")[2].substring(0, 2),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content area
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Title with pin indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item?.isItemPinned == true) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle, // pinned icon
                            contentDescription = "Pinned",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    Text(
                        text = item?.title ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Subtitle
                if (item?.subTitle!!.isNotEmpty()) {
                    Text(
                        text = item.subTitle,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Attachment preview chips
                if (item.attachments!!.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        (item.attachments as List<String?>).forEach { attachment ->
                            AttachmentChip(attachment!!)
                        }
                    }
                }
            }

            // Priority indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = when (item?.priority) {
                            2 -> Color.Red
                            1 -> Color.Yellow
                            else -> Color.Green
                        },
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun AttachmentChip(attachmentPath: String) {
    val fileType = when {
        attachmentPath.contains(".jpg") || attachmentPath.contains(".png") -> "image"
        attachmentPath.contains(".py") -> "code"
        attachmentPath.contains(".txt") -> "text"
        else -> "file"
    }

    val (icon, color) = when (fileType) {
        "image" -> Icons.Default.AccountBox to Color(0xFF74B9FF)
        "code" -> Icons.Default.Email to Color(0xFFA55EEA)
        "text" -> Icons.Default.Email to Color(0xFF26DE81)
        else -> Icons.Filled.Build to Color(0xFF778CA3)
    }

    Row(
        modifier = Modifier
            .height(20.dp)
            .background(color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = fileType,
            modifier = Modifier.size(dp12),
            tint = color
        )
        Spacer(modifier = Modifier.width(dp4))
        Text(
            text = attachmentPath.substringAfterLast('/').take(8) + if (attachmentPath.substringAfterLast('/').length > 8) "..." else "",
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun calculateTimeProximity(dateTime: String): Float {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val eventTime = try {
        LocalDateTime.parse(dateTime, formatter)
    } catch (e: Exception) {
        return 0.0f // Invalid format
    }

    val now = LocalDateTime.now()
    val durationUntilEvent = Duration.between(now, eventTime)

    // If event is in the past
    if (durationUntilEvent.isNegative) return 0.0f

    val totalWindowHours = 24f
    val hoursUntilEvent = durationUntilEvent.toMinutes().toFloat() / 60f

    return when {
        hoursUntilEvent >= totalWindowHours -> 0.0f
        else -> 1.0f - (hoursUntilEvent / totalWindowHours)
    }
}

@Preview
@Composable
fun PreviewScheduleListScreen() {
    /*val sampleSchedules = listOf(
        ScheduleItem(
            id = 1, title = "Date", subTitle = "First Date at night", dateTime = "2025-04-18 21:28",
            lastScheduleOn = "09-04-2025 11:29:42 AM", priority = 2, isItemPinned = true,
            isArchived = true, attachments = listOf("image.jpg", "file.pdf")
        ),
        ScheduleItem(
            id = 1, title = "Interview", subTitle = "Interview in XYZ corp.", dateTime = "2025-04-20 12:00",
            lastScheduleOn = "09-04-2025 11:29:42 AM", priority = 1, isItemPinned = true,
            isArchived = false, attachments = listOf("image.jpg", "file.pdf")
        ),
    )*/
    //ScheduleListScreen(sampleSchedules)
}
