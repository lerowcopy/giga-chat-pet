package com.example.giga_chat_pet.domain.usecase

import com.example.giga_chat_pet.domain.model.ChatMessage
import com.example.giga_chat_pet.domain.repository.ChatRepository
import javax.inject.Inject

class RetrySendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        messageId: Long,
        text: String,
        conversationId: Long,
        conversationHistory: List<ChatMessage>
    ): Result<ChatMessage> {
        return chatRepository.retrySendMessage(messageId, text, conversationHistory, conversationId)
    }
}
