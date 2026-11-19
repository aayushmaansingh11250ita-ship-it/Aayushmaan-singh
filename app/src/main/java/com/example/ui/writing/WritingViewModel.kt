package com.example.ui.writing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.NovaApplication
import com.example.data.NovaRepository
import com.example.data.ai.AiRepository
import com.example.data.ai.WritingFormat
import com.example.data.ai.WritingTone
import com.example.data.local.WritingDraftEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WritingUiState(
    val prompt: String = "",
    val selectedFormat: WritingFormat = WritingFormat.EMAIL,
    val selectedTone: WritingTone = WritingTone.PROFESSIONAL,
    val isGenerating: Boolean = false,
    val generatedOutput: String = "",
    val savedDrafts: List<WritingDraftEntity> = emptyList(),
    val errorMessage: String? = null
)

class WritingViewModel(
    private val novaRepository: NovaRepository,
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WritingUiState())
    val uiState: StateFlow<WritingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            novaRepository.observeWritingDrafts().collect { drafts ->
                _uiState.update { it.copy(savedDrafts = drafts) }
            }
        }
    }

    fun updatePrompt(text: String) {
        _uiState.update { it.copy(prompt = text, errorMessage = null) }
    }

    fun selectFormat(format: WritingFormat) {
        _uiState.update { it.copy(selectedFormat = format) }
    }

    fun selectTone(tone: WritingTone) {
        _uiState.update { it.copy(selectedTone = tone) }
    }

    fun generateDraft() {
        val prompt = _uiState.value.prompt.trim()
        if (prompt.isEmpty() || _uiState.value.isGenerating) return

        val format = _uiState.value.selectedFormat
        val tone = _uiState.value.selectedTone

        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, errorMessage = null) }
            try {
                val output = aiRepository.generateWriting(prompt, format, tone)
                novaRepository.saveWritingDraft(
                    title = "${format.label}: ${prompt.take(30)}",
                    format = format.label,
                    tone = tone.label,
                    prompt = prompt,
                    output = output
                )
                _uiState.update { it.copy(isGenerating = false, generatedOutput = output) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        errorMessage = e.localizedMessage ?: "Failed to draft content."
                    )
                }
            }
        }
    }

    fun loadDraft(draft: WritingDraftEntity) {
        _uiState.update {
            it.copy(
                prompt = draft.prompt,
                generatedOutput = draft.output
            )
        }
    }

    fun deleteDraft(draftId: String) {
        viewModelScope.launch {
            novaRepository.deleteWritingDraft(draftId)
        }
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NovaApplication
                WritingViewModel(
                    novaRepository = app.container.novaRepository,
                    aiRepository = app.container.aiRepository
                )
            }
        }
    }
}
