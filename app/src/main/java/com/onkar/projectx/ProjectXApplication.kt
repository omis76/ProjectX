package com.onkar.projectx

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class ProjectXApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any other necessary components here
    }
}