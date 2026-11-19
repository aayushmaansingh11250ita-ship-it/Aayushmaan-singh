package com.example

import android.app.Application
import com.example.data.NovaRepository
import com.example.data.ai.AiRepository
import com.example.data.ai.PlaceholderAiRepository
import com.example.data.local.NovaDatabase
import com.example.data.preferences.SettingsRepository

class AppContainer(application: Application) {
    private val database: NovaDatabase by lazy { NovaDatabase.getInstance(application) }
    val novaRepository: NovaRepository by lazy { NovaRepository(database.novaDao()) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(application) }
    val aiRepository: AiRepository by lazy { PlaceholderAiRepository() }
}

class NovaApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
