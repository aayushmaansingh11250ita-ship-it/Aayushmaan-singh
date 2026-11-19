package com.example.ui.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.NovaApplication
import com.example.data.NovaRepository
import com.example.data.ai.AiRepository
import com.example.data.ai.StudyGenerationResult
import com.example.data.ai.StudyType
import com.example.data.local.StudyItemEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudyUiState(
    val topicInput: String = "",
    val selectedType: StudyType = StudyType.EXPLAIN,
    val isLoading: Boolean = false,
    val result: StudyGenerationResult? = null,
    val savedItems: List<StudyItemEntity> = emptyList(),
    val selectedQuizAnswers: Map<Int, Int> = emptyMap(),
    val errorMessage: String? = null
)

class StudyViewModel(
    private val novaRepository: NovaRepository,
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudyUiState())
    val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            novaRepository.observeStudyItems().collect { items ->
                _uiState.update { it.copy(savedItems = items) }
            }
        }
    }

    fun updateTopic(topic: String) {
        _uiState.update { it.copy(topicInput = topic, errorMessage = null) }
    }

    fun selectType(type: StudyType) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun generateStudyMaterial(overrideTopic: String? = null) {
        val topic = (overrideTopic ?: _uiState.value.topicInput).trim()
        if (topic.isEmpty() || _uiState.value.isLoading) return

        val type = _uiState.value.selectedType
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    topicInput = topic,
                    isLoading = true,
                    selectedQuizAnswers = emptyMap(),
                    errorMessage = null
                )
            }
            try {
                val result = aiRepository.generateStudyMaterial(topic, type)
                novaRepository.saveStudyItem(
                    topic = result.topic,
                    mode = result.type.label,
                    content = result.summary
                )
                _uiState.update { it.copy(isLoading = false, result = result) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Failed to generate study material."
                    )
                }
            }
        }
    }

    fun selectQuizAnswer(questionIndex: Int, optionIndex: Int) {
        _uiState.update { state ->
            state.copy(selectedQuizAnswers = state.selectedQuizAnswers + (questionIndex to optionIndex))
        }
    }

    fun deleteSavedItem(itemId: String) {
        viewModelScope.launch {
            novaRepository.deleteStudyItem(itemId)
        }
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NovaApplication
                StudyViewModel(
                    novaRepository = app.container.novaRepository,
                    aiRepository = app.container.aiRepository
                )
            }
        }
    }
}
