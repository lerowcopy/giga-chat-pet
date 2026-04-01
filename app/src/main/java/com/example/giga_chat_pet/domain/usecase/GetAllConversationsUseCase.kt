package com.example.giga_chat_pet.domain.usecase

import androidx.paging.PagingData
import com.example.giga_chat_pet.domain.model.Conversation
import com.example.giga_chat_pet.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllConversationsUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    operator fun invoke(): Flow<PagingData<Conversation>> {
        return conversationRepository.getConversations(query = null)
    }
}
