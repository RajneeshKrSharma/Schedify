package com.unique.schedify.core.presentation.download_and_save_ui.model

import com.unique.schedify.core.presentation.download_and_save_ui.utility.PermissionState


data class ConsentCardModel(
    val visibility: Boolean = true,
    val cardTitle: String,
    val cardSubTitle: String,
    val cardButtonText: String,
    val permissionState: PermissionState,
    val isCardButtonEnabled: Boolean,
    val isPermissionGranted: Boolean = true,
    val onCardButtonClick: () -> Unit
)
