package com.example.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.NovaApplication
import com.example.data.NovaRepository
import com.example.data.ai.AiRepository
import com.example.data.ai.ImageAspectRatio
import com.example.data.ai.ImageGenerationResult
import com.example.data.ai.ImageStyle
import com.example.data.local.GeneratedImageEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateUiState(
    val prompt: String = "",
    val selectedStyle: ImageStyle = ImageStyle.CYBERPUNK,
    val selectedRatio: ImageAspectRatio = ImageAspectRatio.SQUARE,
    val isGenerating: Boolean = false,
    val latestResult: ImageGenerationResult? = null,
    val gallery: List<GeneratedImageEntity> = emptyList(),
    val errorMessage: String? = null,
    val isLiveGeminiConnected: Boolean = false
)

class CreateViewModel(
    private val novaRepository: NovaRepository,
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CreateUiState(isLiveGeminiConnected = aiRepository.isLiveGeminiConnected)
    )
    val uiState: StateFlow<CreateUiState> = _uiState.asStateFlow()

    private val promptSuggestions = listOf(
        "Neon violet cyberpunk observatory perched above misty mountains at midnight",
        "Bioluminescent crystal lotus glowing inside a dark obsidian cavern",
        "Minimalist cosmic plasma orb floating above a calm dark navy ocean",
        "Futuristic Tokyo rooftop garden lit by purple holographic lanterns in rain"
    )
    private var suggestionIndex = 0

    init {
        viewModelScope.launch {
            novaRepository.observeGeneratedImages().collect { images ->
                _uiState.update { it.copy(gallery = images) }
            }
        }
    }

    fun updatePrompt(prompt: String) {
        _uiState.update { it.copy(prompt = prompt, errorMessage = null) }
    }

    fun inspireMe() {
        val next = promptSuggestions[suggestionIndex % promptSuggestions.size]
        suggestionIndex++
        _uiState.update { it.copy(prompt = next, errorMessage = null) }
    }

    fun selectStyle(style: ImageStyle) {
        _uiState.update { it.copy(selectedStyle = style) }
    }

    fun selectRatio(ratio: ImageAspectRatio) {
        _uiState.update { it.copy(selectedRatio = ratio) }
    }

    fun generateImage() {
        val prompt = _uiState.value.prompt.trim()
        if (prompt.isEmpty() || _uiState.value.isGenerating) return

        val style = _uiState.value.selectedStyle
        val ratio = _uiState.value.selectedRatio

        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, errorMessage = null) }
            try {
                val result = aiRepository.generateImagePreview(prompt, style, ratio)
                novaRepository.saveGeneratedImage(
                    prompt = result.prompt,
                    style = result.style.label,
                    aspectRatio = result.aspectRatio.label,
                    drawableKey = result.drawableKey
                )
                _uiState.update { it.copy(isGenerating = false, latestResult = result) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        errorMessage = e.localizedMessage ?: "Failed to generate preview."
                    )
                }
            }
        }
    }

    fun toggleFavorite(imageId: String) {
        viewModelScope.launch {
            novaRepository.toggleImageFavorite(imageId)
        }
    }

    fun deleteImage(imageId: String) {
        viewModelScope.launch {
            novaRepository.deleteGeneratedImage(imageId)
        }
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NovaApplication
                CreateViewModel(
                    novaRepository = app.container.novaRepository,
                    aiRepository = app.container.aiRepository
                )
            }
        }
    }
}
