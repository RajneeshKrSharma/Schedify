package com.unique.schedify.post_auth.post_auth_loading

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.core.network.utility.USER_CONSENTS
import com.unique.schedify.core.presentation.download_and_save_ui.DownloadAndSaveConsents
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens

class PostAuthDownloadAndSaveConsents(
    val title: String
) : DownloadAndSaveConsents() {
    override fun title(): String = title

    override fun imageUrl(): String = USER_CONSENTS

    override fun buttonText(): String = "Yup, Continue"

    override fun proceedToScreen(): AvailableScreens =
        AvailableScreens.PostAuth.PostAuthDownloadAndSaveScreen
}

@Composable
fun PostAuthConsentUiScreen(
    navController: NavController,
) {
    PostAuthDownloadAndSaveConsents(
        title = stringResource(R.string.great_you_have_logged_in_required_some_remaining_consents)
    ).DownloadAndSaveConsentsScreen(
        navController = navController,
    )
}