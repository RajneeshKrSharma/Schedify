package com.unique.schedify.post_auth.post_auth_loading

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.unique.schedify.core.domain.workers.DownloadAndSaveWorker
import com.unique.schedify.core.presentation.download_and_save_ui.DownloadAndSaveUi
import com.unique.schedify.core.presentation.download_and_save_ui.utility.ApisList
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens

class PostAuthScreenClass : DownloadAndSaveUi() {
    override fun title(): String = "Your data is getting ready for smooth experience,"

    override fun apisToCall(): List<String> = ApisList.postAuthApisList.map { it.name }

    override fun proceedToScreen(): AvailableScreens = AvailableScreens.PostAuth.HomeScreen

    override fun workerTagName(): String = DownloadAndSaveWorker.POST_AUTH_TAG
}

@Composable
fun PostAuthDownloadAndSaveUiScreen(
    navController: NavController,
) {
    PostAuthScreenClass().ComposeDownloadAndSaveUi(
        navController = navController,
        topAppBar = null,
        content = {
        }
    )
}