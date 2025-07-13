package com.unique.schedify.post_auth.user_mapped_weather.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unique.schedify.core.presentation.base_composables.BaseCompose
import com.unique.schedify.core.presentation.common_composables.DashedDivider
import com.unique.schedify.core.presentation.common_composables.GradientButton
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp24
import com.unique.schedify.core.presentation.utils.size_units.dp8

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMappedWeatherScreen(
    navController: NavController,
    userMappedWeatherViewmodel: UserMappedWeatherViewmodel = hiltViewModel()
) {

    BaseCompose(
        modifier = Modifier,
        navController = navController,
        topBar = {
            TopAppBar(
                title = { Text(text = "Auto Re-Schedule Worker Status") },
                modifier = Modifier.padding(dp16),
            )
        },
    ) {

        val context = LocalContext.current

        val workerData by remember{ mutableStateOf(userMappedWeatherViewmodel.workerData) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dp16),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            GradientButton(
                text = "Start Auto Re-Schedule Worker"
            ) {
                userMappedWeatherViewmodel.startTestWorker(context = context)
            }
            Spacer(Modifier.height(dp24))
            Text(text = "Worker Status Data",
                style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(dp8))
            LazyColumn{
                items(workerData.value.size) { index ->
                    val item = workerData.value[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${item.id}",
                            style = MaterialTheme.typography.headlineMedium)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize()
                                .padding(dp16)
                        ) {
                            Text(text = "${item.title}")
                            Text(text = "${item.subtitle}")
                            Text(text = "${item.image}")
                        }
                    }
                    DashedDivider(
                        modifier = Modifier.fillMaxWidth(),
                        dividerColor = MaterialTheme.colorScheme.inversePrimary,
                        vSpace = dp8
                    )
                }
            }
        }
    }
}