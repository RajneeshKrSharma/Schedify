package com.unique.schedify.core.di

import com.unique.schedify.core.domain.workers.DownloadAndSaveWorker
import com.unique.schedify.core.presentation.download_and_save_ui.utility.ApiHandler
import com.unique.schedify.core.presentation.download_and_save_ui.utility.PostAuthApiHandler
import com.unique.schedify.core.presentation.download_and_save_ui.utility.PreAuthApiHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@Module
@InstallIn(SingletonComponent::class)
interface ApiHandlerModule {

    @Binds
    @IntoMap
    @StringKey(DownloadAndSaveWorker.PRE_AUTH_TAG)
    fun bindPreAuthHandler(handler: PreAuthApiHandler): ApiHandler

    @Binds
    @IntoMap
    @StringKey(DownloadAndSaveWorker.POST_AUTH_TAG)
    fun bindPostAuthHandler(handler: PostAuthApiHandler): ApiHandler
}