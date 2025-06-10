package com.unique.schedify.core.di

import com.unique.schedify.core.presentation.download_and_save_ui.utility.ApiHandler
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ApiHandlerEntryPoint {
    val handlerMap: Map<String, @JvmSuppressWildcards ApiHandler>
}