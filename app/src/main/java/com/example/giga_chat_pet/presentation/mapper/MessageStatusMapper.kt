package com.example.giga_chat_pet.presentation.mapper

import com.example.giga_chat_pet.domain.model.MessageStatus
import com.example.giga_chat_pet.presentation.chat.MessageStatusUiModel

object MessageStatusMapper {

    fun toUi(domain: MessageStatus): MessageStatusUiModel {
        return when (domain) {
            MessageStatus.SENDING -> MessageStatusUiModel.SENDING
            MessageStatus.SENT -> MessageStatusUiModel.SENT
            MessageStatus.ERROR -> MessageStatusUiModel.ERROR
        }
    }

    fun toDomain(ui: MessageStatusUiModel): MessageStatus {
        return when (ui) {
            MessageStatusUiModel.SENDING -> MessageStatus.SENDING
            MessageStatusUiModel.SENT -> MessageStatus.SENT
            MessageStatusUiModel.ERROR -> MessageStatus.ERROR
        }
    }
}
