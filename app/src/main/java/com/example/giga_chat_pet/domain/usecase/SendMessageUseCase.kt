package com.example.giga_chat_pet.domain.usecase

import com.example.giga_chat_pet.domain.model.ChatMessage
import com.example.giga_chat_pet.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        text: String,
        conversationId: Long,
        conversationHistory: List<ChatMessage>
    ): Result<ChatMessage> {
        return chatRepository.sendMessage(text, conversationHistory, conversationId)
    }
}
