package com.example.giga_chat_pet.domain.repository

import androidx.paging.PagingData
import com.example.giga_chat_pet.domain.model.ChatMessage
import com.example.giga_chat_pet.domain.model.Conversation
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun getConversations(query: String? = null): Flow<PagingData<Conversation>>
    suspend fun createConversation(title: String): Long
    suspend fun deleteConversation(id: Long)
    suspend fun updateConversationTitle(id: Long, title: String)
    suspend fun updateConversationLastMessage(id: Long, lastMessageText: String, timestamp: Long)
    fun getConversationMessages(conversationId: Long): Flow<List<ChatMessage>>
}
