package com.example.ui.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.NovaApplication
import com.example.data.NovaRepository
import com.example.data.ai.AiRepository
import com.example.data.local.AssistantMode
import com.example.data.preferences.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class VoicePhase {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING
}

data class VoiceTurn(
    val id: String,
    val userTranscript: String,
    val assistantReply: String
)

data class VoiceUiState(
    val phase: VoicePhase = VoicePhase.IDLE,
    val liveTranscript: String = "",
    val turns: List<VoiceTurn> = emptyList(),
    val autoSpeakEnabled: Boolean = true,
    val statusNotice: String? = null,
    val isLiveGeminiConnected: Boolean = false
)

class VoiceViewModel(
    private val novaRepository: NovaRepository,
    private val settingsRepository: SettingsRepository,
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        VoiceUiState(isLiveGeminiConnected = aiRepository.isLiveGeminiConnected)
    )
    val uiState: StateFlow<VoiceUiState> = _uiState.asStateFlow()

    private var voiceSessionId: String? = null

    init {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                _uiState.update { it.copy(autoSpeakEnabled = settings.voiceAutoSpeak) }
            }
        }
    }

    fun onListeningStarted() {
        _uiState.update { it.copy(phase = VoicePhase.LISTENING, liveTranscript = "", statusNotice = null) }
    }

    fun onPartialTranscript(text: String) {
        _uiState.update { it.copy(liveTranscript = text) }
    }

    fun onSpeechError(message: String) {
        _uiState.update { it.copy(phase = VoicePhase.IDLE, statusNotice = message) }
    }

    fun onSpeakingStarted() {
        _uiState.update { it.copy(phase = VoicePhase.SPEAKING) }
    }

    fun onSpeakingFinished() {
        _uiState.update { it.copy(phase = VoicePhase.IDLE) }
    }

    fun toggleAutoSpeak(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateVoiceAutoSpeak(enabled)
        }
    }

    fun submitTranscript(transcript: String, onSpeakReply: (String) -> Unit) {
        val clean = transcript.trim()
        if (clean.isEmpty()) {
            _uiState.update { it.copy(phase = VoicePhase.IDLE) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(phase = VoicePhase.THINKING, liveTranscript = clean, statusNotice = null) }
            val reply = aiRepository.generateVoiceReply(clean)

            val session = novaRepository.createOrUpdateSession(
                sessionId = voiceSessionId,
                title = "Voice: ${clean.take(32)}",
                mode = AssistantMode.VOICE,
                lastPreview = reply
            )
            voiceSessionId = session.id
            novaRepository.addMessage(session.id, "user", clean, isSamplePreview = false)
            novaRepository.addMessage(session.id, "assistant", reply, isSamplePreview = !aiRepository.isLiveGeminiConnected)

            val turn = VoiceTurn(
                id = "${System.currentTimeMillis()}",
                userTranscript = clean,
                assistantReply = reply
            )
            val shouldSpeak = settingsRepository.settingsFlow.first().voiceAutoSpeak
            _uiState.update {
                it.copy(
                    phase = if (shouldSpeak) VoicePhase.SPEAKING else VoicePhase.IDLE,
                    turns = listOf(turn) + it.turns
                )
            }
            if (shouldSpeak) {
                onSpeakReply(reply)
            }
        }
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NovaApplication
                VoiceViewModel(
                    novaRepository = app.container.novaRepository,
                    settingsRepository = app.container.settingsRepository,
                    aiRepository = app.container.aiRepository
                )
            }
        }
    }
}
