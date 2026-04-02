package com.example.giga_chat_pet.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.giga_chat_pet.domain.model.MessageStatus
import com.example.giga_chat_pet.domain.usecase.ClearChatHistoryUseCase
import com.example.giga_chat_pet.domain.usecase.GetMessagesUseCase
import com.example.giga_chat_pet.domain.usecase.RetrySendMessageUseCase
import com.example.giga_chat_pet.domain.usecase.SendMessageUseCase
import com.example.giga_chat_pet.presentation.chat.ChatUiState
import com.example.giga_chat_pet.presentation.mapper.ChatMessageToUiModel
import com.example.giga_chat_pet.presentation.mapper.MessageStatusMapper
import com.example.giga_chat_pet.presentation.renderer.MarkdownRenderer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val retrySendMessageUseCase: RetrySendMessageUseCase,
    private val clearChatHistoryUseCase: ClearChatHistoryUseCase,
    private val markdownRenderer: MarkdownRenderer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val conversationId: Long = checkNotNull(savedStateHandle["conversationId"])

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            getMessagesUseCase(conversationId).collect { messages ->
                _uiState.update { currentState ->
                    currentState.copy(messages = messages.map { ChatMessageToUiModel.map(it) })
                }
            }
        }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty() || _uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, inputText = "") }

            val currentMessages = _uiState.value.messages.map { model ->
                com.example.giga_chat_pet.domain.model.ChatMessage(
                    id = model.id,
                    text = model.text,
                    isFromMe = model.isFromMe,
                    timestamp = model.timestamp,
                    status = MessageStatusMapper.toDomain(model.status)
                )
            }

            val result = sendMessageUseCase(text, conversationId, currentMessages)

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

    fun retryFailedMessage(messageId: Long, text: String) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val currentMessages = _uiState.value.messages.map { model ->
                com.example.giga_chat_pet.domain.model.ChatMessage(
                    id = model.id,
                    text = model.text,
                    isFromMe = model.isFromMe,
                    timestamp = model.timestamp,
                    status = MessageStatusMapper.toDomain(model.status)
                )
            }

            val result = retrySendMessageUseCase(messageId, text, conversationId, currentMessages)

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
            clearChatHistoryUseCase(conversationId)
        }
    }

    fun getMarkdownRenderer(): MarkdownRenderer = markdownRenderer
}
