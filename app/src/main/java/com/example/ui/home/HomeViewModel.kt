package com.example.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.NovaApplication
import com.example.data.NovaRepository
import com.example.data.ai.AiRepository
import com.example.data.local.ChatSessionEntity
import com.example.data.preferences.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val displayName: String = "Cosmic Explorer",
    val isLiveGeminiConnected: Boolean = false,
    val recentSessions: List<ChatSessionEntity> = emptyList(),
    val savedImageCount: Int = 0,
    val studyDeckCount: Int = 0
)

class HomeViewModel(
    novaRepository: NovaRepository,
    settingsRepository: SettingsRepository,
    aiRepository: AiRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        settingsRepository.settingsFlow,
        novaRepository.observeSessions(),
        novaRepository.observeGeneratedImages(),
        novaRepository.observeStudyItems()
    ) { settings, sessions, images, studyItems ->
        HomeUiState(
            displayName = settings.displayName,
            isLiveGeminiConnected = aiRepository.isLiveGeminiConnected,
            recentSessions = sessions.take(4),
            savedImageCount = images.size,
            studyDeckCount = studyItems.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = HomeUiState()
    )

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NovaApplication
                HomeViewModel(
                    novaRepository = app.container.novaRepository,
                    settingsRepository = app.container.settingsRepository,
                    aiRepository = app.container.aiRepository
                )
            }
        }
    }
}
