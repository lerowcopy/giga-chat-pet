package com.example.giga_chat_pet.domain.usecase

import com.example.giga_chat_pet.domain.repository.ConversationRepository
import javax.inject.Inject

class UpdateConversationTitleUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(id: Long, title: String) {
        conversationRepository.updateConversationTitle(id, title)
    }
}
