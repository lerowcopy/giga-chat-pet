package com.example.giga_chat_pet.data.repository

import com.example.giga_chat_pet.data.local.ChatDatabase
import com.example.giga_chat_pet.data.local.LocalMessage
import com.example.giga_chat_pet.data.local.MessageStatus as LocalMessageStatus
import com.example.giga_chat_pet.data.model.ChatRequest
import com.example.giga_chat_pet.data.model.MessageDto
import com.example.giga_chat_pet.data.remote.GigaChatApi
import com.example.giga_chat_pet.domain.model.ChatMessage
import com.example.giga_chat_pet.domain.model.MessageStatus
import com.example.giga_chat_pet.domain.repository.ChatRepository
import com.example.giga_chat_pet.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val api: GigaChatApi,
    private val database: ChatDatabase,
    private val conversationRepository: ConversationRepository
) : ChatRepository {

    private val dao = database.messageDao()

    override fun getMessages(): Flow<List<ChatMessage>> {
        return dao.getAllMessages().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getMessagesByConversationId(conversationId: Long): Flow<List<ChatMessage>> {
        return dao.getMessagesByConversationId(conversationId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun sendMessage(
        text: String,
        conversationHistory: List<ChatMessage>,
        conversationId: Long
    ): Result<ChatMessage> {
        return sendMessageWithRetry(
            text = text,
            conversationHistory = conversationHistory,
            messageId = null,
            retryCount = 0,
            conversationId = conversationId
        )
    }

    override suspend fun retrySendMessage(
        messageId: Long,
        text: String,
        conversationHistory: List<ChatMessage>,
        conversationId: Long
    ): Result<ChatMessage> {
        return sendMessageWithRetry(
            text = text,
            conversationHistory = conversationHistory,
            messageId = messageId,
            retryCount = 0,
            conversationId = conversationId
        )
    }

    private suspend fun sendMessageWithRetry(
        text: String,
        conversationHistory: List<ChatMessage>,
        messageId: Long?,
        retryCount: Int,
        conversationId: Long
    ): Result<ChatMessage> {
        val maxRetries = 3
        val baseDelayMs = 1000L

        val userMessageId = messageId ?: run {
            val userMessage = LocalMessage(
                text = text,
                isFromMe = true,
                status = LocalMessageStatus.SENDING,
                conversationId = conversationId
            )
            dao.insertMessage(userMessage)
        }

        dao.updateMessageStatus(userMessageId, LocalMessageStatus.SENDING)

        val messagesForApi = conversationHistory
            .takeLast(10)
            .map { MessageDto(if (it.isFromMe) "user" else "assistant", it.text) }
            .plus(MessageDto.user(text))

        return try {
            val response = api.sendMessage(
                ChatRequest(
                    model = "GigaChat",
                    messages = messagesForApi
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val assistantText = response.body()!!.choices.first().message.content

                dao.updateMessageStatus(userMessageId, LocalMessageStatus.SENT)

                val assistantMessage = LocalMessage(
                    text = assistantText,
                    isFromMe = false,
                    status = LocalMessageStatus.SENT,
                    conversationId = conversationId
                )
                dao.insertMessage(assistantMessage)

                conversationRepository.updateConversationLastMessage(
                    id = conversationId,
                    lastMessageText = assistantText,
                    timestamp = System.currentTimeMillis()
                )

                Result.success(assistantMessage.toDomainModel())
            } else {
                val error = Exception("API error: ${response.code()} ${response.message()}")
                handleSendError(userMessageId, text, conversationHistory, error, retryCount, maxRetries, baseDelayMs, conversationId)
            }
        } catch (e: Exception) {
            handleSendError(userMessageId, text, conversationHistory, e, retryCount, maxRetries, baseDelayMs, conversationId)
        }
    }

    private suspend fun handleSendError(
        messageId: Long,
        text: String,
        conversationHistory: List<ChatMessage>,
        error: Exception,
        retryCount: Int,
        maxRetries: Int,
        baseDelayMs: Long,
        conversationId: Long
    ): Result<ChatMessage> {
        return if (retryCount < maxRetries) {
            val delayMs = baseDelayMs * (1L shl retryCount)
            kotlinx.coroutines.delay(delayMs)
            sendMessageWithRetry(text, conversationHistory, messageId, retryCount + 1, conversationId)
        } else {
            dao.updateMessageStatus(messageId, LocalMessageStatus.ERROR)
            Result.failure(error)
        }
    }

    override suspend fun updateMessageStatus(id: Long, status: MessageStatus) {
        dao.updateMessageStatus(id, status.toLocalStatus())
    }

    override suspend fun clearHistory(conversationId: Long) {
        dao.deleteMessagesByConversationId(conversationId)
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

    private fun MessageStatus.toLocalStatus(): LocalMessageStatus {
        return when (this) {
            MessageStatus.SENDING -> LocalMessageStatus.SENDING
            MessageStatus.SENT -> LocalMessageStatus.SENT
            MessageStatus.ERROR -> LocalMessageStatus.ERROR
        }
    }
}
