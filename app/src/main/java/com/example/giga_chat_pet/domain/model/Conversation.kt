package com.example.giga_chat_pet.domain.model

data class Conversation(
    val id: Long,
    val title: String,
    val createdAt: Long,
    val lastMessageAt: Long,
    val lastMessageText: String
)
