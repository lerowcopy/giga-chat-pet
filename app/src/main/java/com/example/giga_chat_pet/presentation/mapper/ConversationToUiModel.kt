package com.example.giga_chat_pet.presentation.mapper

import com.example.giga_chat_pet.domain.model.Conversation
import com.example.giga_chat_pet.presentation.chatlist.ConversationUiModel

object ConversationToUiModel {
    fun map(domain: Conversation): ConversationUiModel {
        return ConversationUiModel(
            id = domain.id,
            title = domain.title,
            createdAt = domain.createdAt,
            lastMessageAt = domain.lastMessageAt,
            lastMessageText = domain.lastMessageText
        )
    }
}
