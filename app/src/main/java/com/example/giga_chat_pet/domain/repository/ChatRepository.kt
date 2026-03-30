package com.example.giga_chat_pet.domain.repository

import com.example.giga_chat_pet.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(): Flow<List<ChatMessage>>
    suspend fun sendMessage(text: String, conversationHistory: List<ChatMessage>): Result<ChatMessage>
    suspend fun updateMessageStatus(id: Long, status: com.example.giga_chat_pet.domain.model.MessageStatus)
    suspend fun clearHistory()
}
