package com.unique.schedify.pre_auth.pre_auth_loading.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.core.network.utility.USER_CONSENTS
import com.unique.schedify.core.presentation.download_and_save_ui.DownloadAndSaveConsents
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens

class PreAuthDownloadAndSaveConsents(
    val title: String
) : DownloadAndSaveConsents() {
    override fun title(): String = title

    override fun imageUrl(): String = USER_CONSENTS

    override fun buttonText(): String = "Yup, Continue"

    override fun proceedToScreen(): AvailableScreens =
        AvailableScreens.PreAuth.PreAuthDownloadAndSaveScreen
}

@Composable
fun PreAuthConsentUiScreen(
    navController: NavController,
) {
    PreAuthDownloadAndSaveConsents(
        title = stringResource(R.string.hi_need_some_consents_before_we_proceed)
    ).DownloadAndSaveConsentsScreen(
        navController = navController,

        )
}