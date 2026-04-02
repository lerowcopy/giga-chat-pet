package com.example.giga_chat_pet.presentation.mapper

import com.example.giga_chat_pet.domain.model.ChatMessage
import com.example.giga_chat_pet.presentation.chat.ChatMessageUiModel

object ChatMessageToUiModel {
    fun map(domain: ChatMessage): ChatMessageUiModel {
        return ChatMessageUiModel(
            id = domain.id,
            text = domain.text,
            isFromMe = domain.isFromMe,
            timestamp = domain.timestamp,
            status = MessageStatusMapper.toUi(domain.status)
        )
    }
}
