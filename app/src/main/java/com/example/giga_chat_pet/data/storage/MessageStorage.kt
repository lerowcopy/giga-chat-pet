package com.example.giga_chat_pet.data.storage

import com.example.giga_chat_pet.data.local.LocalMessage
import com.example.giga_chat_pet.data.local.MessageDao
import com.example.giga_chat_pet.data.local.MessageStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageStorage @Inject constructor(
    private val messageDao: MessageDao
) {

    fun getAllMessages(): Flow<List<LocalMessage>> {
        return messageDao.getAllMessages()
    }

    fun getMessagesByConversationId(conversationId: Long): Flow<List<LocalMessage>> {
        return messageDao.getMessagesByConversationId(conversationId)
    }

    suspend fun insertMessage(message: LocalMessage): Long {
        return messageDao.insertMessage(message)
    }

    suspend fun updateMessageStatus(id: Long, status: MessageStatus) {
        messageDao.updateMessageStatus(id, status)
    }

    suspend fun deleteMessagesByConversationId(conversationId: Long) {
        messageDao.deleteMessagesByConversationId(conversationId)
    }
}
