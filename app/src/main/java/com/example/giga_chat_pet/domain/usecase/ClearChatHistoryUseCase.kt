package com.example.giga_chat_pet.domain.usecase

import com.example.giga_chat_pet.domain.repository.ChatRepository
import javax.inject.Inject

class ClearChatHistoryUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(conversationId: Long) {
        chatRepository.clearHistory(conversationId)
    }
}
