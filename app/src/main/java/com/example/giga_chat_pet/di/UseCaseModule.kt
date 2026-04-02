package com.example.giga_chat_pet.di

import com.example.giga_chat_pet.domain.usecase.ClearChatHistoryUseCase
import com.example.giga_chat_pet.domain.usecase.GetMessagesUseCase
import com.example.giga_chat_pet.domain.usecase.RetrySendMessageUseCase
import com.example.giga_chat_pet.domain.usecase.SendMessageUseCase
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.Provides
import com.example.giga_chat_pet.domain.repository.ChatRepository

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetMessagesUseCase(
        chatRepository: ChatRepository
    ): GetMessagesUseCase {
        return GetMessagesUseCase(chatRepository)
    }

    @Provides
    @Singleton
    fun provideSendMessageUseCase(
        chatRepository: ChatRepository
    ): SendMessageUseCase {
        return SendMessageUseCase(chatRepository)
    }

    @Provides
    @Singleton
    fun provideRetrySendMessageUseCase(
        chatRepository: ChatRepository
    ): RetrySendMessageUseCase {
        return RetrySendMessageUseCase(chatRepository)
    }

    @Provides
    @Singleton
    fun provideClearChatHistoryUseCase(
        chatRepository: ChatRepository
    ): ClearChatHistoryUseCase {
        return ClearChatHistoryUseCase(chatRepository)
    }
}
