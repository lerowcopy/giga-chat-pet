package com.example.giga_chat_pet.presentation.chatlist

import androidx.paging.PagingData
import com.example.giga_chat_pet.presentation.chat.MessageStatusUiModel

data class ChatListUiState(
    val conversations: PagingData<ConversationUiModel>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ConversationUiModel(
    val id: Long,
    val title: String,
    val createdAt: Long,
    val lastMessageAt: Long,
    val lastMessageText: String
)
