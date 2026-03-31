package com.example.giga_chat_pet.domain.repository

import com.example.giga_chat_pet.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(): Flow<List<ChatMessage>>
    fun getMessagesByConversationId(conversationId: Long): Flow<List<ChatMessage>>
    suspend fun sendMessage(text: String, conversationHistory: List<ChatMessage>, conversationId: Long): Result<ChatMessage>
    suspend fun retrySendMessage(messageId: Long, text: String, conversationHistory: List<ChatMessage>, conversationId: Long): Result<ChatMessage>
    suspend fun updateMessageStatus(id: Long, status: com.example.giga_chat_pet.domain.model.MessageStatus)
    suspend fun clearHistory(conversationId: Long)
}
