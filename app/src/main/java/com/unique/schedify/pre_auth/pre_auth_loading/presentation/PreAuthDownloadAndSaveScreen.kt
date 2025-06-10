package com.unique.schedify.pre_auth.pre_auth_loading.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.unique.schedify.core.domain.workers.DownloadAndSaveWorker
import com.unique.schedify.core.presentation.download_and_save_ui.DownloadAndSaveUi
import com.unique.schedify.core.presentation.download_and_save_ui.utility.ApisList
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens

class PreAuthScreenClass : DownloadAndSaveUi() {
    override fun title(): String = "Fetching and getting things ready for you, Have patience . . ."

    override fun apisToCall(): List<String> = ApisList.preAuthApisList.map { it.name }

    override fun proceedToScreen(): AvailableScreens = AvailableScreens.PreAuth.LoginScreen

    override fun workerTagName(): String = DownloadAndSaveWorker.PRE_AUTH_TAG
}

@Composable
fun PreAuthDownloadAndSaveUiScreen(
    navController: NavController,
) {
    PreAuthScreenClass().ComposeDownloadAndSaveUi(
        navController = navController,
        topAppBar = null,
        content = {}
    )
}