package com.unique.schedify.post_auth.schedule_list.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unique.schedify.core.presentation.utils.size_units.dp12
import com.unique.schedify.core.presentation.utils.size_units.dp2
import com.unique.schedify.core.presentation.utils.size_units.dp24
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.ScheduleListResponseDto


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDetailScreen(
    item: ScheduleListResponseDto.Data,
    onBack: () -> Unit
) {

    data class WeatherInfo(
        val day: String,
        val icon: ImageVector,
        val temperature: String,
        val description: String
    )


    val todayWeather = WeatherInfo("Today", Icons.Default.WbSunny, "28°C", "Partly Cloudy")
    val forecast = listOf(
        WeatherInfo("Mon", Icons.Default.WbSunny, "30°C", "Sunny"),
        WeatherInfo("Tue", Icons.Default.Cloud, "27°C", "Rainy"),
        WeatherInfo("Wed", Icons.Default.CloudQueue, "25°C", "Cloudy"),
        WeatherInfo("Thu", Icons.Default.WbSunny, "29°C", "Sunny"),
        WeatherInfo("Fri", Icons.Default.FlashOn, "26°C", "Storm")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 🌟 Hero Gradient Bubble
            // 🌟 Combined DateTime + Weather Animated Section
            item {
                val transition = rememberInfiniteTransition()
                val animatedOffset = transition.animateFloat(
                    initialValue = 0f,
                    targetValue = 6f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2500, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(horizontal = 16.dp)
                        .graphicsLayer { translationY = animatedOffset.value }
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    when (item.priority) {
                                        2 -> Color(0xFFFF6B6B)
                                        1 -> Color(0xFFFFC048)
                                        else -> Color(0xFF4ECDC4)
                                    },
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        ),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))

                        // Circular Time Bubble
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .shadow(6.dp, shape = CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            when (item.priority) {
                                                2 -> Color(0xFFFF9F9F)
                                                1 -> Color(0xFFFFD166)
                                                else -> Color(0xFF4ECDC4)
                                            },
                                            when (item.priority) {
                                                2 -> Color(0xFFEE5253)
                                                1 -> Color(0xFFFF9F43)
                                                else -> Color(0xFF1DD1A1)
                                            }
                                        )
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = item.dateTime?.split(" ")?.get(1)?.substring(0, 5) ?: "--:--",
                                    style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                                )
                                Text(
                                    text = item.dateTime?.split("-")?.getOrNull(2)?.substring(0, 2) ?: "--",
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Weather Summary
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(90.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF2F8)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Icon(
                                    imageVector = todayWeather.icon,
                                    contentDescription = null,
                                    tint = Color(0xFF3498DB),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = todayWeather.temperature,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = todayWeather.description,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Title and Pin
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    if (true) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = item.title.orEmpty(),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Subtitle
            item {
                item.subTitle?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Last scheduled
            item {
                Text(
                    text = "Last scheduled: ${item.lastScheduleOn.orEmpty()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // 🌦 Weather Section
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Today's Weather",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF2F8))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = todayWeather.icon,
                                contentDescription = null,
                                tint = Color(0xFF3498DB),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = todayWeather.temperature, fontWeight = FontWeight.Bold)
                                Text(
                                    text = todayWeather.description,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Next 5 Days",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(forecast) { dayWeather ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier
                                    .width(90.dp)
                                    .height(110.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Text(text = dayWeather.day, style = MaterialTheme.typography.labelSmall)
                                    Icon(
                                        imageVector = dayWeather.icon,
                                        contentDescription = null,
                                        tint = Color(0xFF5DADE2),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = dayWeather.temperature,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = dayWeather.description,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 📎 Attachments
            if (!item.attachments.isNullOrEmpty()) {
                item {
                    Text(
                        text = "Attachments",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                items(item.attachments as List<String?>) { attachment ->
                    AttachmentCard(attachment.toString())
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

    }
}


@Composable
fun AttachmentCard(attachmentPath: String) {
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

    Card (
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = dp2)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dp12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = fileType,
                modifier = Modifier.size(dp24),
                tint = color
            )
            Spacer(modifier = Modifier.width(dp12))
            Text(
                text = attachmentPath.substringAfterLast('/'),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { /* Handle download/open */ }) {
                Icon(Icons.AutoMirrored.Rounded.ExitToApp, contentDescription = "Open") // OpenInNew
            }
        }
    }
}