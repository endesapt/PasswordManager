package com.example.passwordmanager

import android.app.Application
import com.example.passwordmanager.app.AppContainer

class PasswordManagerApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
