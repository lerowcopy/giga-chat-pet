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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(
    private val api: GigaChatApi,
    private val database: ChatDatabase
) : ChatRepository {

    private val dao = database.messageDao()

    override fun getMessages(): Flow<List<ChatMessage>> {
        return dao.getAllMessages().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun sendMessage(
        text: String,
        conversationHistory: List<ChatMessage>
    ): Result<ChatMessage> {
        // Создаём локальное сообщение пользователя
        val userMessage = LocalMessage(
            text = text,
            isFromMe = true,
            status = LocalMessageStatus.SENDING
        )
        val userMessageId = dao.insertMessage(userMessage)

        // Формируем историю для отправки в API
        val messagesForApi = conversationHistory
            .takeLast(10) // Ограничиваем контекст последними 10 сообщениями
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

                // Обновляем статус сообщения пользователя
                dao.updateMessageStatus(userMessageId, LocalMessageStatus.SENT)

                // Сохраняем ответ ассистента
                val assistantMessage = LocalMessage(
                    text = assistantText,
                    isFromMe = false,
                    status = LocalMessageStatus.SENT
                )
                dao.insertMessage(assistantMessage)

                Result.success(assistantMessage.toDomainModel())
            } else {
                // Ошибка API
                dao.updateMessageStatus(userMessageId, LocalMessageStatus.ERROR)
                Result.failure(Exception("API error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            dao.updateMessageStatus(userMessageId, LocalMessageStatus.ERROR)
            Result.failure(e)
        }
    }

    override suspend fun updateMessageStatus(id: Long, status: MessageStatus) {
        dao.updateMessageStatus(id, status.toLocalStatus())
    }

    override suspend fun clearHistory() {
        dao.deleteAllMessages()
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
