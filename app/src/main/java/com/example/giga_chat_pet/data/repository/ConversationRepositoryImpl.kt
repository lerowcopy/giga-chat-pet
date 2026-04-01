package com.example.giga_chat_pet.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.giga_chat_pet.data.local.ChatDatabase
import com.example.giga_chat_pet.data.local.LocalConversation
import com.example.giga_chat_pet.data.local.LocalMessage
import com.example.giga_chat_pet.data.local.MessageStatus as LocalMessageStatus
import com.example.giga_chat_pet.domain.model.ChatMessage
import com.example.giga_chat_pet.domain.model.Conversation
import com.example.giga_chat_pet.domain.model.MessageStatus
import com.example.giga_chat_pet.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepositoryImpl @Inject constructor(
    private val database: ChatDatabase
) : ConversationRepository {

    private val conversationDao = database.conversationDao()
    private val messageDao = database.messageDao()

    override fun getConversations(query: String?): Flow<PagingData<Conversation>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                if (query.isNullOrBlank()) {
                    conversationDao.getAllConversations()
                } else {
                    conversationDao.searchConversations(query)
                }
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomainModel() }
        }
    }

    override suspend fun createConversation(title: String): Long {
        val conversation = LocalConversation(
            title = title,
            createdAt = System.currentTimeMillis(),
            lastMessageAt = System.currentTimeMillis()
        )
        return conversationDao.insertConversation(conversation)
    }

    override suspend fun deleteConversation(id: Long) {
        conversationDao.deleteConversation(id)
        messageDao.deleteMessagesByConversationId(id)
    }

    override suspend fun updateConversationTitle(id: Long, title: String) {
        conversationDao.updateConversationTitle(id, title)
    }

    override suspend fun updateConversationLastMessage(
        id: Long,
        lastMessageText: String,
        timestamp: Long
    ) {
        val conversation = conversationDao.getConversationById(id) ?: return
        val updated = conversation.copy(
            lastMessageText = lastMessageText,
            lastMessageAt = timestamp
        )
        conversationDao.updateConversation(updated)
    }

    override fun getConversationMessages(conversationId: Long): Flow<List<ChatMessage>> {
        return messageDao.getMessagesByConversationId(conversationId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    private fun LocalConversation.toDomainModel(): Conversation {
        return Conversation(
            id = id,
            title = title,
            createdAt = createdAt,
            lastMessageAt = lastMessageAt,
            lastMessageText = lastMessageText
        )
    }

    private fun LocalMessage.toDomainModel(): ChatMessage {
        return ChatMessage(
            id = id,
            text = text,
            isFromMe = isFromMe,
            timestamp = timestamp,
            status = status.toDomainStatus()
        )
    }

    private fun LocalMessageStatus.toDomainStatus(): MessageStatus {
        return when (this) {
            LocalMessageStatus.SENDING -> MessageStatus.SENDING
            LocalMessageStatus.SENT -> MessageStatus.SENT
            LocalMessageStatus.ERROR -> MessageStatus.ERROR
        }
    }
}
