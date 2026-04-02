package com.example.giga_chat_pet.data.mapper

import com.example.giga_chat_pet.data.local.LocalConversation
import com.example.giga_chat_pet.domain.model.Conversation

object ConversationToLocalConversation {
    fun map(domain: Conversation): LocalConversation {
        return LocalConversation(
            id = domain.id,
            title = domain.title,
            createdAt = domain.createdAt,
            lastMessageAt = domain.lastMessageAt,
            lastMessageText = domain.lastMessageText
        )
    }
}
