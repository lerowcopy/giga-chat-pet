package com.example.giga_chat_pet.domain.model

data class ChatMessage(
    val id: Long,
    val text: String,
    val isFromMe: Boolean,
    val timestamp: Long,
    val status: MessageStatus
)
