package com.example.giga_chat_pet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.giga_chat_pet.domain.usecase.GetAllConversationsUseCase
import com.example.giga_chat_pet.domain.usecase.SearchConversationsUseCase
import com.example.giga_chat_pet.domain.usecase.UpdateConversationTitleUseCase
import com.example.giga_chat_pet.presentation.mapper.ConversationToUiModel
import com.example.giga_chat_pet.presentation.chatlist.ChatListUiState
import com.example.giga_chat_pet.presentation.chatlist.ConversationUiModel
import com.example.giga_chat_pet.domain.repository.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalStdlibApi::class)
@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val getAllConversationsUseCase: GetAllConversationsUseCase,
    private val searchConversationsUseCase: SearchConversationsUseCase,
    private val updateConversationTitleUseCase: UpdateConversationTitleUseCase
) : ViewModel() {

    private val searchQuery = MutableStateFlow<String?>(null)

    val conversations: Flow<PagingData<ConversationUiModel>> = searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isNullOrBlank()) {
                getAllConversationsUseCase()
            } else {
                searchConversationsUseCase(query)
            }
        }
        .map { pagingData: PagingData<com.example.giga_chat_pet.domain.model.Conversation> ->
            pagingData.map { conversation ->
                ConversationToUiModel.map(conversation)
            }
        }
        .cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    fun search(query: String) {
        searchQuery.value = query
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

    suspend fun updateConversationTitle(id: Long, title: String) {
        updateConversationTitleUseCase(id, title)
    }
}
