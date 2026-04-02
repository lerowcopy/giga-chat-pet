package com.example.giga_chat_pet.data.mapper

import com.example.giga_chat_pet.data.local.LocalConversation
import com.example.giga_chat_pet.domain.model.Conversation

object LocalConversationToConversation {
    fun map(local: LocalConversation): Conversation {
        return Conversation(
            id = local.id,
            title = local.title,
            createdAt = local.createdAt,
            lastMessageAt = local.lastMessageAt,
            lastMessageText = local.lastMessageText
        )
    }
}
