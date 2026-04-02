package com.example.giga_chat_pet.data.mapper

import com.example.giga_chat_pet.data.local.LocalMessage
import com.example.giga_chat_pet.data.local.MessageStatus as LocalMessageStatus
import com.example.giga_chat_pet.domain.model.ChatMessage
import com.example.giga_chat_pet.domain.model.MessageStatus

object ChatMessageMapper {

    fun toDomain(local: LocalMessage): ChatMessage {
        return ChatMessage(
            id = local.id,
            text = local.text,
            isFromMe = local.isFromMe,
            timestamp = local.timestamp,
            status = local.status.toDomainStatus()
        )
    }

    fun toLocal(domain: ChatMessage): LocalMessage {
        return LocalMessage(
            id = domain.id,
            text = domain.text,
            isFromMe = domain.isFromMe,
            timestamp = domain.timestamp,
            status = domain.status.toLocalStatus()
        )
    }

    private fun LocalMessageStatus.toDomainStatus(): MessageStatus {
        return when (this) {
            LocalMessageStatus.SENDING -> MessageStatus.SENDING
            LocalMessageStatus.SENT -> MessageStatus.SENT
            LocalMessageStatus.ERROR -> MessageStatus.ERROR
        }
    }

    private fun MessageStatus.toLocalStatus(): LocalMessageStatus {
        return when (this) {
            MessageStatus.SENDING -> LocalMessageStatus.SENDING
            MessageStatus.SENT -> LocalMessageStatus.SENT
            MessageStatus.ERROR -> LocalMessageStatus.ERROR
        }
    }
}
