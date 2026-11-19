package com.example.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.NovaApplication
import com.example.data.NovaRepository
import com.example.data.ai.AiRepository
import com.example.data.local.AssistantMode
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ChatSessionEntity
import com.example.data.preferences.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val activeSessionId: String? = null,
    val sessionTitle: String = "New Conversation",
    val activeMode: AssistantMode = AssistantMode.CHAT,
    val messages: List<ChatMessageEntity> = emptyList(),
    val streamingReply: String = "",
    val isGenerating: Boolean = false,
    val inputText: String = "",
    val errorMessage: String? = null,
    val recentSessions: List<ChatSessionEntity> = emptyList(),
    val isLiveGeminiConnected: Boolean = false
)

class ChatViewModel(
    private val novaRepository: NovaRepository,
    private val settingsRepository: SettingsRepository,
    private val aiRepository: AiRepository,
    initialSessionId: String?,
    initialPrompt: String?,
    initialMode: AssistantMode
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ChatUiState(
            activeSessionId = initialSessionId,
            activeMode = initialMode,
            isLiveGeminiConnected = aiRepository.isLiveGeminiConnected
        )
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var messageObserverJob: Job? = null
    private var generationJob: Job? = null

    init {
        viewModelScope.launch {
            novaRepository.observeSessions().collect { sessions ->
                _uiState.update { it.copy(recentSessions = sessions) }
            }
        }
        if (!initialSessionId.isNullOrBlank()) {
            loadSession(initialSessionId)
        } else if (!initialPrompt.isNullOrBlank()) {
            sendMessage(initialPrompt)
        }
    }

    fun updateInput(text: String) {
        _uiState.update { it.copy(inputText = text, errorMessage = null) }
    }

    fun selectMode(mode: AssistantMode) {
        _uiState.update { it.copy(activeMode = mode) }
    }

    fun startNewSession() {
        generationJob?.cancel()
        messageObserverJob?.cancel()
        _uiState.update {
            it.copy(
                activeSessionId = null,
                sessionTitle = "New Conversation",
                messages = emptyList(),
                streamingReply = "",
                isGenerating = false,
                errorMessage = null
            )
        }
    }

    fun loadSession(sessionId: String) {
        generationJob?.cancel()
        messageObserverJob?.cancel()
        viewModelScope.launch {
            val session = novaRepository.getSession(sessionId)
            val mode = session?.mode?.let {
                runCatching { AssistantMode.valueOf(it) }.getOrDefault(AssistantMode.CHAT)
            } ?: AssistantMode.CHAT

            _uiState.update {
                it.copy(
                    activeSessionId = sessionId,
                    sessionTitle = session?.title ?: "Conversation",
                    activeMode = mode,
                    streamingReply = "",
                    isGenerating = false
                )
            }

            messageObserverJob = launch {
                novaRepository.observeMessages(sessionId).collect { msgs ->
                    _uiState.update { it.copy(messages = msgs) }
                }
            }
        }
    }

    fun sendMessage(overridePrompt: String? = null) {
        val prompt = (overridePrompt ?: _uiState.value.inputText).trim()
        if (prompt.isEmpty() || _uiState.value.isGenerating) return

        _uiState.update { it.copy(inputText = "", isGenerating = true, streamingReply = "", errorMessage = null) }

        generationJob = viewModelScope.launch {
            try {
                val currentMode = _uiState.value.activeMode
                val existingSessionId = _uiState.value.activeSessionId
                val session = novaRepository.createOrUpdateSession(
                    sessionId = existingSessionId,
                    title = prompt.take(40),
                    mode = currentMode,
                    lastPreview = prompt
                )

                if (existingSessionId == null) {
                    _uiState.update {
                        it.copy(activeSessionId = session.id, sessionTitle = session.title)
                    }
                    messageObserverJob?.cancel()
                    messageObserverJob = launch {
                        novaRepository.observeMessages(session.id).collect { msgs ->
                            _uiState.update { it.copy(messages = msgs) }
                        }
                    }
                }

                novaRepository.addMessage(
                    sessionId = session.id,
                    role = "user",
                    content = prompt,
                    isSamplePreview = false
                )

                val history = novaRepository.getMessages(session.id)
                val settings = settingsRepository.settingsFlow.first()
                var finalReply = ""

                aiRepository.streamChatReply(
                    prompt = prompt,
                    history = history,
                    mode = currentMode,
                    creativity = settings.responseCreativity,
                    streamEnabled = settings.streamResponses
                ).collect { partial ->
                    finalReply = partial
                    _uiState.update { it.copy(streamingReply = partial) }
                }

                if (finalReply.isNotBlank()) {
                    novaRepository.addMessage(
                        sessionId = session.id,
                        role = "assistant",
                        content = finalReply,
                        isSamplePreview = !aiRepository.isLiveGeminiConnected
                    )
                    novaRepository.createOrUpdateSession(
                        sessionId = session.id,
                        title = session.title,
                        mode = currentMode,
                        lastPreview = finalReply
                    )
                }
                _uiState.update { it.copy(isGenerating = false, streamingReply = "") }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        streamingReply = "",
                        errorMessage = e.localizedMessage ?: "Unable to generate response."
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            initialSessionId: String?,
            initialPrompt: String?,
            initialMode: AssistantMode
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NovaApplication
                ChatViewModel(
                    novaRepository = app.container.novaRepository,
                    settingsRepository = app.container.settingsRepository,
                    aiRepository = app.container.aiRepository,
                    initialSessionId = initialSessionId,
                    initialPrompt = initialPrompt,
                    initialMode = initialMode
                )
            }
        }
    }
}
