package com.unique.schedify.core.presentation.download_and_save_ui.model

sealed class PostLoginPreRequisitesEvents {
    data object NavigateToPincode : PostLoginPreRequisitesEvents()
    data object NavigateToProceedScreen : PostLoginPreRequisitesEvents()
}