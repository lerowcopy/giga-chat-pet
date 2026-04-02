package com.example.giga_chat_pet.domain.usecase

import com.example.giga_chat_pet.domain.model.ChatMessage
import com.example.giga_chat_pet.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(conversationId: Long): Flow<List<ChatMessage>> {
        return chatRepository.getMessagesByConversationId(conversationId)
    }
}
