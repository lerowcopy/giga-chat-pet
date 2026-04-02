package com.example.giga_chat_pet.presentation.chat

data class ChatUiState(
    val messages: List<ChatMessageUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val inputText: String = ""
)

data class ChatMessageUiModel(
    val id: Long,
    val text: String,
    val isFromMe: Boolean,
    val timestamp: Long,
    val status: MessageStatusUiModel
)

enum class MessageStatusUiModel {
    SENDING,
    SENT,
    ERROR
}
