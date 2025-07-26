package com.unique.schedify.post_auth.address.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.core.presentation.base_composables.BaseCompose
import com.unique.schedify.core.presentation.common_composables.CustomSearchBar
import com.unique.schedify.core.presentation.common_composables.CustomTopAppBar
import com.unique.schedify.core.presentation.common_composables.DashedDivider
import com.unique.schedify.core.presentation.common_composables.FormBuilder
import com.unique.schedify.core.presentation.common_composables.LoadingUi
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp12
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp2
import com.unique.schedify.core.presentation.utils.size_units.dp4
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import com.unique.schedify.core.presentation.utils.ui_utils.showToast
import com.unique.schedify.core.util.Resource
import com.unique.schedify.core.util.permissions.PermissionHelper
import com.unique.schedify.core.util.permissions.RECORD_AUDIO_PERMISSION
import com.unique.schedify.post_auth.address.data.remote.model.PincodeUiConfig
import com.unique.schedify.post_auth.address.presentation.utils.PincodeFields
import com.unique.schedify.post_auth.address.presentation.utils.buildPincodeFormFields

@Composable
fun PincodeScreen(
    navController: NavController,
    addressViewmodel: AddressViewmodel = hiltViewModel()
) {

    val context = LocalContext.current
    val updatedEnteredPincode = rememberUpdatedState(
        newValue = addressViewmodel.pincodeInputByUserState.value
    )
    val addAddressState = addressViewmodel.addAddressState.value
    val formState by remember {
        derivedStateOf {
            buildPincodeFormFields(pincodeValue = updatedEnteredPincode.value)
        }
    }
    val formFields = rememberUpdatedState(newValue = formState)
    val formResultedData: MutableState<Map<String, String>> = remember { mutableStateOf(mapOf()) }
    val focusManager = LocalFocusManager.current
    val scrollState: LazyListState = rememberLazyListState()

    val isSearchedPerformed by rememberUpdatedState(newValue = addressViewmodel.isSearchingInProgressState)
    // this "isSearchedPerformed" is to determine have user perform searching with any char.

    LaunchedEffect(scrollState) {
        snapshotFlow {
            Pair(scrollState.firstVisibleItemIndex, scrollState.firstVisibleItemScrollOffset)
        }.collect { (index, offset) ->
            addressViewmodel.isSearchBoxSectionVisibleState.value =
                (index > 0 || offset > 100) || (isSearchedPerformed.value)
        }
    }

    LaunchedEffect(addAddressState) {
        if (addAddressState is Resource.Success) {
            Navigation.navigateAndClearBackStackScreen(
                navigateTo = AvailableScreens.PostAuth.HomeScreen,
                navController = navController
            )
        } else if (addAddressState is Resource.Error) {
            context.showToast(msg = addAddressState.message ?: context.getString(R.string.something_went_wrong))
            addressViewmodel.resetAddressState()
        }
    }

    val enterTransition = remember {
        fadeIn(animationSpec = tween(durationMillis = 400, delayMillis = 200)) +
                expandVertically(animationSpec = tween(durationMillis = 400, delayMillis = 200))
    }

    val exitTransition = remember {
        fadeOut(animationSpec = tween(durationMillis = 400, delayMillis = 200)) +
                shrinkVertically(animationSpec = tween(durationMillis = 400, delayMillis = 200))
    }

    val isUserRequestedForMic = remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && isUserRequestedForMic.value) {
                if(PermissionHelper.isPermissionPending(
                        context = context,
                        permissionName = RECORD_AUDIO_PERMISSION
                    ).not()) {
                    context.showToast(context.getString(R.string.microphone_permission_received))

                } else {
                    context.showToast(context.getString(R.string.microphone_permission_denied))
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    BaseCompose(
        navController = navController,
        topBar = {
            CustomTopAppBar(
                title = stringResource(R.string.pincode)
            )
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(dp12)
            ) {

                AnimatedVisibility(
                    visible = addressViewmodel.isSearchBoxSectionVisibleState.value,
                    enter = enterTransition,
                    exit = exitTransition
                ) {
                    CustomSearchBar(
                        hint = stringResource(R.string.search_places),
                        onSearch = { query ->
                            addressViewmodel.searchingInProgress(isSearchingInProgress = query.isNotEmpty())
                            addressViewmodel.searchAddressData(query = query)
                        },
                        onMicClicked = {
                            isUserRequestedForMic.value = true
                        }
                    )
                }

                AnimatedVisibility(
                    visible = addressViewmodel.isSearchBoxSectionVisibleState.value.not(),
                    enter = enterTransition,
                    exit = exitTransition
                ) {
                    LaunchedEffect(formResultedData.value[PincodeFields.PINCODE.name]) {
                        addressViewmodel.pincodeInputByUserState.value =
                            formResultedData.value[PincodeFields.PINCODE.name].orEmpty()
                    }

                    Column {
                        Text(
                            stringResource(R.string.it_s_time_to_set_your_location),
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        )

                        Spacer(modifier = Modifier.height(dp8))

                        FormBuilder(
                            fields = formFields.value,
                            onFormChanged = { formData ->
                                formResultedData.value = formData
                                addressViewmodel.resetAllState()
                            },
                            onDone = {
                                proceedOnValidPincode(
                                    formResultedData = formResultedData,
                                    onDataPrepared = { pincode ->
                                        focusManager.clearFocus(force = true)
                                        addressViewmodel.fetchPincodes(pincode = pincode)
                                    }
                                )
                            }
                        )

                        /*
                        In emulator sometimes keyboard is not visible, then for just testing use this,
                        afterward ensure to comment ! */
                        /*GradientButton(text = "find") {
                            proceedOnValidPincode(
                                formResultedData = formResultedData,
                                onDataPrepared = { pincode ->
                                    focusManager.clearFocus(force = true)
                                    addressViewmodel.fetchPincodes(pincode = pincode)
                                }
                            )
                        }*/

                        DashedDivider(vSpace = dp4)
                    }
                }

                SuccessOrErrorUi(
                    addressViewmodel = addressViewmodel,
                    scrollState = scrollState
                )
            }

            LoadLoadingUi(addressViewmodel)
        }
    }
}

fun proceedOnValidPincode(
    formResultedData: MutableState<Map<String, String>>,
    onDataPrepared: (pincode: String) -> Unit,
) {
    val inputPincode = formResultedData.value[PincodeFields.PINCODE.name].orEmpty()
    onDataPrepared(inputPincode)
}

@Composable
fun LoadLoadingUi(addressViewmodel: AddressViewmodel) {
    if (addressViewmodel.pincodeApiDataState.value is Resource.Loading ||
        addressViewmodel.addAddressState.value is Resource.Loading
    ) {
        LoadingUi()
    }
}

@Composable
fun SuccessOrErrorUi(addressViewmodel: AddressViewmodel, scrollState: LazyListState) {
    val state = addressViewmodel.pincodeApiDataState.value

    if (state is Resource.Success) {
        state.data?.let { pincodeConfigData ->
            if (pincodeConfigData != PincodeUiConfig.default()) {
                if (pincodeConfigData.postOfficeList.isNotEmpty()) {
                    Column {

                        Spacer(modifier = Modifier.padding(vertical = dp4))

                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = MaterialTheme.typography.bodyLarge.toSpanStyle().copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                ) { append(pincodeConfigData.message) }
                                withStyle(
                                    style = MaterialTheme.typography.bodySmall.toSpanStyle().copy(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                ) {
                                    append(
                                        stringResource(
                                            R.string.pick_your_a_location_or_nearby_location_to_get_weather_updates_on_scheduled_items_if_any
                                        )
                                    )
                                }
                            })

                        Spacer(modifier = Modifier.height(dp16))

                        PostOfficeListUi(
                            scrollState = scrollState,
                            postOffices = pincodeConfigData.postOfficeList,
                            onSelectAddressDetail = { postOfficeConfig ->
                                addressViewmodel.addAddress(
                                    postOfficeConfig = postOfficeConfig
                                )
                            }
                        )
                    }

                } else {
                    Text(pincodeConfigData.message)
                }

            } else Text(stringResource(id = R.string.no_data_available))
        } ?: Text(stringResource(id = R.string.no_data_available))

    } else if (state is Resource.Error) {
        Text(
            state.message
                ?: state.stringResourceId?.let { errorMsgId -> stringResource(id = errorMsgId) }
                ?: stringResource(R.string.something_went_wrong))
    }
}


@Composable
fun PostOfficeListUi(
    scrollState: LazyListState,
    postOffices: List<PincodeUiConfig.PostOfficeConfig>,
    onSelectAddressDetail: (PincodeUiConfig.PostOfficeConfig) -> Unit
) {
    val selectedPostOffice = remember { mutableStateOf<PincodeUiConfig.PostOfficeConfig?>(null) }

    LazyColumn(
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(dp12),
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            top = dp12,
            bottom = dp12
        )
    ) {
        items(postOffices) { postOffice ->
            PostOfficeCard(
                postOffice = postOffice,
                isSelected = selectedPostOffice.value == postOffice,
                onSelect = { postOfficeConfig ->
                    selectedPostOffice.value = postOfficeConfig
                    onSelectAddressDetail.invoke(postOfficeConfig)
                }
            )
        }
    }
}

@Composable
fun PostOfficeCard(
    postOffice: PincodeUiConfig.PostOfficeConfig,
    isSelected: Boolean,
    onSelect: (PincodeUiConfig.PostOfficeConfig) -> Unit
) {

    val labelStyle = MaterialTheme.typography.bodySmall.toSpanStyle().copy(
        color = MaterialTheme.colorScheme.inversePrimary
    )

    val valueStyle = MaterialTheme.typography.bodySmall.toSpanStyle().copy(
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
    )

    Card(
        modifier = Modifier
            .wrapContentSize(),
        shape = RoundedCornerShape(dp8),
        elevation = CardDefaults.cardElevation(dp4),
        colors = CardDefaults.cardColors(
            containerColor =
            MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .clickable { onSelect(postOffice) }
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = isSelected,
                    onClick = { onSelect(postOffice) },
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(dp12)
                ) {

                    Text(
                        text = postOffice.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(dp8),
                        verticalArrangement = Arrangement.spacedBy(dp2)
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = labelStyle) { append("Block: ") }
                                withStyle(style = valueStyle) { append(postOffice.block) }
                            })
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = labelStyle) { append("Region: ") }
                                withStyle(style = valueStyle) { append(postOffice.region) }
                            })
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = labelStyle) { append("District: ") }
                                withStyle(style = valueStyle) { append(postOffice.district) }
                            })
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = labelStyle) { append("Division: ") }
                                withStyle(style = valueStyle) { append(postOffice.division) }
                            })
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = labelStyle) { append("State: ") }
                                withStyle(style = valueStyle) { append(postOffice.state) }
                            })
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = labelStyle) { append("Country: ") }
                                withStyle(style = valueStyle) { append(postOffice.country) }
                            })
                    }
                }
            }
        }
    }
}



