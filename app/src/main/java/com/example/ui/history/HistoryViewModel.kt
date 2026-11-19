package com.example.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.NovaApplication
import com.example.data.NovaRepository
import com.example.data.local.AssistantMode
import com.example.data.local.ChatSessionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryUiState(
    val searchQuery: String = "",
    val selectedModeFilter: AssistantMode? = null,
    val sessions: List<ChatSessionEntity> = emptyList()
)

class HistoryViewModel(
    private val novaRepository: NovaRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val modeFilter = MutableStateFlow<AssistantMode?>(null)

    val uiState: StateFlow<HistoryUiState> = combine(
        novaRepository.observeSessions(),
        searchQuery,
        modeFilter
    ) { allSessions, query, filter ->
        val filtered = allSessions.filter { session ->
            val matchesMode = filter == null || session.mode == filter.name
            val matchesQuery = query.isBlank() ||
                session.title.contains(query, ignoreCase = true) ||
                session.lastMessagePreview.contains(query, ignoreCase = true)
            matchesMode && matchesQuery
        }
        HistoryUiState(
            searchQuery = query,
            selectedModeFilter = filter,
            sessions = filtered
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = HistoryUiState()
    )

    fun updateSearch(query: String) {
        searchQuery.value = query
    }

    fun selectModeFilter(mode: AssistantMode?) {
        modeFilter.value = mode
    }

    fun togglePin(session: ChatSessionEntity) {
        viewModelScope.launch {
            novaRepository.togglePinSession(session)
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            novaRepository.deleteSession(sessionId)
        }
    }

    companion object {
        fun provideFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NovaApplication
                HistoryViewModel(app.container.novaRepository)
            }
        }
    }
}
