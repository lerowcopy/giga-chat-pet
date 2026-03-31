package com.example.giga_chat_pet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.giga_chat_pet.domain.model.Conversation
import com.example.giga_chat_pet.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatListUiState(
    val conversations: Flow<PagingData<Conversation>> = MutableStateFlow(PagingData.empty()),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatListViewModel(
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    private fun loadConversations() {
        _uiState.value = _uiState.value.copy(
            conversations = conversationRepository.getConversations().cachedIn(viewModelScope)
        )
    }

    fun createConversation(): Long {
        var conversationId = 0L
        viewModelScope.launch {
            conversationId = conversationRepository.createConversation("New Chat")
        }
        return conversationId
    }

    suspend fun deleteConversation(id: Long) {
        conversationRepository.deleteConversation(id)
    }

    companion object {
        fun provideFactory(conversationRepository: ConversationRepository): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    ChatListViewModel(conversationRepository)
                }
            }
        }
    }
}
