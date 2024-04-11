package com.azamovhudstc.soplay.app

import android.app.Application
import com.azamovhudstc.soplay.theme.ThemeManager
import com.azamovhudstc.soplay.utils.initializeNetwork
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    companion object{
        lateinit var instance:App
    }
    override fun onCreate() {
        super.onCreate()
        instance=this
        initializeNetwork(this)
    }
}