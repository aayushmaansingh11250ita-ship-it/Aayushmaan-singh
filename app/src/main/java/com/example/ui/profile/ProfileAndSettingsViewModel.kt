package com.example.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.NovaApplication
import com.example.data.NovaRepository
import com.example.data.ai.AiRepository
import com.example.data.preferences.SettingsRepository
import com.example.data.preferences.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val settings: UserSettings = UserSettings(),
    val totalChats: Int = 0,
    val totalImages: Int = 0,
    val totalStudyDecks: Int = 0,
    val totalDrafts: Int = 0,
    val isLiveGeminiConnected: Boolean = false
)

class ProfileViewModel(
    private val novaRepository: NovaRepository,
    private val settingsRepository: SettingsRepository,
    aiRepository: AiRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        settingsRepository.settingsFlow,
        novaRepository.observeSessions(),
        novaRepository.observeGeneratedImages(),
        novaRepository.observeStudyItems(),
        novaRepository.observeWritingDrafts()
    ) { settings, sessions, images, study, drafts ->
        ProfileUiState(
            settings = settings,
            totalChats = sessions.size,
            totalImages = images.size,
            totalStudyDecks = study.size,
            totalDrafts = drafts.size,
            isLiveGeminiConnected = aiRepository.isLiveGeminiConnected
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ProfileUiState()
    )

    fun updateDisplayName(name: String) {
        viewModelScope.launch {
            settingsRepository.updateDisplayName(name)
        }
    }

    fun updateCreativity(value: Float) {
        viewModelScope.launch {
            settingsRepository.updateCreativity(value)
        }
    }

    fun updateStreamResponses(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateStreamResponses(enabled)
        }
    }

    fun updateVoiceAutoSpeak(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateVoiceAutoSpeak(enabled)
        }
    }

    fun updateHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateHapticFeedback(enabled)
        }
    }

    fun updateDarkCosmicMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateDarkCosmicMode(enabled)
        }
    }

    fun clearAllLocalData() {
        viewModelScope.launch {
            novaRepository.clearAllLocalData()
        }
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NovaApplication
                ProfileViewModel(
                    novaRepository = app.container.novaRepository,
                    settingsRepository = app.container.settingsRepository,
                    aiRepository = app.container.aiRepository
                )
            }
        }
    }
}
