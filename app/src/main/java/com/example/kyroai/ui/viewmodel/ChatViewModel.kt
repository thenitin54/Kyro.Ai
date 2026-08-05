package com.example.kyroai.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kyroai.data.local.ChatMessageEntity
import com.example.kyroai.data.local.ChatSessionEntity
import com.example.kyroai.data.local.KyroDatabase
import com.example.kyroai.data.local.UserPreferences
import com.example.kyroai.data.repository.ChatRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val database = KyroDatabase.getInstance(application)
    val userPreferences = UserPreferences(application)
    private val repository = ChatRepository(database.chatDao(), userPreferences, application)

    val sessions: StateFlow<List<ChatSessionEntity>> = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId: StateFlow<String?> = _currentSessionId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentMessages: StateFlow<List<ChatMessageEntity>> = _currentSessionId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.getMessagesForSession(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showSettingsModal = MutableStateFlow(false)
    val showSettingsModal: StateFlow<Boolean> = _showSettingsModal.asStateFlow()

    private val _showProfileModal = MutableStateFlow(false)
    val showProfileModal: StateFlow<Boolean> = _showProfileModal.asStateFlow()

    private val _attachedImageUri = MutableStateFlow<String?>(null)
    val attachedImageUri: StateFlow<String?> = _attachedImageUri.asStateFlow()

    init {
        viewModelScope.launch {
            sessions.collect { list ->
                if (list.isNotEmpty() && _currentSessionId.value == null) {
                    _currentSessionId.value = list.first().sessionId
                }
            }
        }
    }

    fun selectSession(sessionId: String) {
        _currentSessionId.value = sessionId
    }

    fun createNewChat() {
        viewModelScope.launch {
            val newId = repository.createNewSession()
            _currentSessionId.value = newId
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val sessionId = _currentSessionId.value
        viewModelScope.launch {
            val activeSessionId = sessionId ?: repository.createNewSession()
            if (_currentSessionId.value == null) {
                _currentSessionId.value = activeSessionId
            }

            _isLoading.value = true
            val currentAttached = _attachedImageUri.value
            _attachedImageUri.value = null

            repository.sendMessage(
                sessionId = activeSessionId,
                userText = text,
                imageUriString = currentAttached,
                history = currentMessages.value
            )
            _isLoading.value = false
        }
    }

    fun pinSession(sessionId: String, isPinned: Boolean) {
        viewModelScope.launch {
            repository.togglePinSession(sessionId, isPinned)
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            if (_currentSessionId.value == sessionId) {
                val remaining = sessions.value.filter { it.sessionId != sessionId }
                _currentSessionId.value = remaining.firstOrNull()?.sessionId
            }
        }
    }

    fun deleteMessage(messageId: Long) {
        viewModelScope.launch {
            repository.deleteMessage(messageId)
        }
    }

    fun updateSessionTitle(sessionId: String, newTitle: String) {
        viewModelScope.launch {
            repository.updateSessionTitle(sessionId, newTitle)
        }
    }

    fun setAttachedImageUri(uri: String?) {
        _attachedImageUri.value = uri
    }

    fun toggleSettingsModal(show: Boolean) {
        _showSettingsModal.value = show
    }

    fun toggleProfileModal(show: Boolean) {
        _showProfileModal.value = show
    }
}
