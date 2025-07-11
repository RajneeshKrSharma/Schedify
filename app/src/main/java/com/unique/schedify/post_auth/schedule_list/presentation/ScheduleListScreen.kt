package com.unique.schedify.post_auth.schedule_list.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.unique.schedify.R
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp2
import com.unique.schedify.core.presentation.utils.size_units.dp20
import com.unique.schedify.core.presentation.utils.size_units.dp3
import com.unique.schedify.core.presentation.utils.size_units.dp300
import com.unique.schedify.core.presentation.utils.size_units.dp4
import com.unique.schedify.core.presentation.utils.size_units.dp6
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.size_units.dp80
import com.unique.schedify.core.presentation.utils.size_units.sp14
import com.unique.schedify.core.presentation.utils.size_units.sp16
import com.unique.schedify.core.presentation.utils.size_units.sp18
import com.unique.schedify.core.util.Resource
import com.unique.schedify.post_auth.schedule_list.AttachmentType
import com.unique.schedify.post_auth.schedule_list.ScheduleTab
import com.unique.schedify.post_auth.schedule_list.remote.dto.ScheduleListResponseDto
import com.unique.schedify.ui.theme.primaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleListScreen(
    navController: NavController,
    viewModel: ScheduleListViewModel,
) {
    val scheduleItems = viewModel.scheduleItems.value
    val tabTitles = ScheduleTab.entries
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var fabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Schedule List Items",
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() } ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(dp16)
            ) {
                // Add Schedule with Current Location FAB
                AnimatedVisibility(
                    visible = fabExpanded,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it }
                ) {
                    FloatingActionButton(
                        onClick = {
                            fabExpanded = false
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Filled.LocationOn, "Current Location", tint = Color.White)
                    }
                }

                // Main FAB
                FloatingActionButton(
                    onClick = { fabExpanded = !fabExpanded },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        if (fabExpanded) Icons.Default.Close else Icons.Default.Add,
                        "Add Schedule",
                        tint = Color.White
                    )
                }
            }
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.secondary)
                .fillMaxSize()
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .height(dp3),
                         color = primaryColor
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        selectedContentColor = primaryColor,
                        unselectedContentColor = Color.Gray,
                        text = {
                            Text(
                                text = tab.title,
                                fontSize = sp14,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    )
                }
            }

            when(viewModel.scheduleItems.value) {
                is Resource.Loading -> {
                    // Show a loading indicator
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = primaryColor)
                    }
                }
                is Resource.Success -> {

                    val scheduleData = scheduleItems.data
                    val allItems = scheduleData
                    val pinnedScheduleItems = scheduleData?.filter { it?.isItemPinned == true }
                    val archivedScheduleItems = scheduleData?.filter { it?.isArchived == true }

                    // Determine which list to display based on the selected tab
                    val currentList = when (selectedTabIndex) {
                        1 -> pinnedScheduleItems ?: emptyList()
                        2 -> archivedScheduleItems ?: emptyList()
                        else -> allItems ?: emptyList()
                    }

                    // Display the list based on the selected tab
                    if(currentList.isNotEmpty()){
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(dp16)
                        ) {
                            items((currentList)) { item ->

                                ItemCard(item)

                            }
                        }
                    } else {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No Items Found",
                                color = Color.Gray,
                                fontSize = sp16
                            )
                        }
                    }
                }
                else -> {
                    // Show an error message
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Schedule Items",
                            color = Color.Red,
                            fontSize = sp16
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCard(schedule: ScheduleListResponseDto.Data?) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("weather_lottie.json")
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dp2
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dp6)
            .clickable {
                // calling new screen with schedule details
            }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dp16),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(dp80)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.size(dp300)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(dp16))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = schedule?.title ?: "--",
                        fontSize = sp18,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(dp4))
                    Text(
                        text = schedule?.subTitle ?: "--",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(dp8))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                         // for testing use this -> listOf("https//.jpg",".pdf",".txt")

                        schedule?.attachments?.forEach { attachmentPath ->
                            attachmentPath?.let {
                                val fileType = when {
                                    attachmentPath.contains(".jpg") || attachmentPath.contains(".png") -> AttachmentType.IMAGE
                                    attachmentPath.contains(".pdf") -> AttachmentType.PDF
                                    attachmentPath.contains(".txt") -> AttachmentType.TEXT
                                    else -> "file"
                                }
                                Icon(
                                    painter = when (fileType) {
                                        AttachmentType.PDF -> painterResource(id = R.drawable.pdf_icon)
                                        AttachmentType.IMAGE -> painterResource(id = R.drawable.img_icon)
                                        AttachmentType.TEXT -> painterResource(id = R.drawable.file_icon)
                                        else -> painterResource(id = R.drawable.file_icon)
                                    },
                                    contentDescription = "$fileType",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(dp20)
                                )
                                Spacer(modifier = Modifier.width(dp4))
                            }
                        }
                    }
                }
            }

            if (schedule?.isItemPinned == true) {
                Icon(
                    painter = painterResource(id = R.drawable.pin_icon), // your pin icon resource
                    contentDescription = "Pinned",
                    tint = primaryColor,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(dp8)
                        .size(dp20)
                        .rotate(45f)
                )
            }
        }
    }
}