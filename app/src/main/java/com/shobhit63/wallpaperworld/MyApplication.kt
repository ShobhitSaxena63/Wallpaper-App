package com.shobhit63.wallpaperworld

import android.app.Application
import android.os.Debug
import timber.log.Timber

class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}