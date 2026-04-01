package com.example.giga_chat_pet.di

import com.example.giga_chat_pet.data.repository.ChatRepositoryImpl
import com.example.giga_chat_pet.data.repository.ConversationRepositoryImpl
import com.example.giga_chat_pet.domain.repository.ChatRepository
import com.example.giga_chat_pet.domain.repository.ConversationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindConversationRepository(
        impl: ConversationRepositoryImpl
    ): ConversationRepository
}
