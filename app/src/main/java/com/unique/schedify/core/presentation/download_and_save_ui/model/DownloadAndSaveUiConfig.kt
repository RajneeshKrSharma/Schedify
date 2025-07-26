package com.unique.schedify.core.presentation.download_and_save_ui.model

import com.unique.schedify.core.presentation.download_and_save_ui.utility.DownloadWorkerStatus

data class DownloadAndSaveUiConfig(
    val data: Set<String>,
    val errorMsg: String,
    val status: DownloadWorkerStatus
) {
    companion object {
        fun default() = DownloadAndSaveUiConfig(
            data = emptySet(),
            errorMsg = "",
            status = DownloadWorkerStatus.INIT
        )
    }
}
