package com.example.giga_chat_pet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.giga_chat_pet.domain.model.ChatMessage
import com.example.giga_chat_pet.domain.model.MessageStatus
import com.example.giga_chat_pet.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val inputText: String = ""
)

class ChatViewModel(
    private val repository: ChatRepository,
    private val conversationId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            repository.getMessagesByConversationId(conversationId).collect { messages ->
                _uiState.update { currentState ->
                    currentState.copy(messages = messages)
                }
            }
        }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty() || _uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, inputText = "") }

            val currentMessages = _uiState.value.messages
            val result = repository.sendMessage(text, currentMessages, conversationId)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Неизвестная ошибка"
                        )
                    }
                }
            )
        }
    }

    fun retryFailedMessage(message: ChatMessage) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val currentMessages = _uiState.value.messages
            val result = repository.retrySendMessage(message.id, message.text, currentMessages, conversationId)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Неизвестная ошибка"
                        )
                    }
                }
            )
        }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory(conversationId)
        }
    }

    companion object {
        fun provideFactory(repository: ChatRepository, conversationId: Long): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    ChatViewModel(repository, conversationId)
                }
            }
        }
    }
}
