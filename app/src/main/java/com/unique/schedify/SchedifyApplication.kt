package com.unique.schedify

import android.app.Application
import com.unique.schedify.core.ConnectivityChecker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SchedifyApplication : Application() {
    @Inject
    lateinit var connectivityChecker: ConnectivityChecker
}