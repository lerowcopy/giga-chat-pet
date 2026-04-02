package com.example.giga_chat_pet.di

import com.example.giga_chat_pet.domain.repository.UserProfileRepository
import com.example.giga_chat_pet.domain.usecase.ClearChatHistoryUseCase
import com.example.giga_chat_pet.domain.usecase.GetMessagesUseCase
import com.example.giga_chat_pet.domain.usecase.GetUserProfileUseCase
import com.example.giga_chat_pet.domain.usecase.RetrySendMessageUseCase
import com.example.giga_chat_pet.domain.usecase.SendMessageUseCase
import com.example.giga_chat_pet.domain.usecase.SignOutUseCase
import com.example.giga_chat_pet.domain.usecase.UpdateUserProfileUseCase
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

    @Provides
    @Singleton
    fun provideGetUserProfileUseCase(
        userProfileRepository: UserProfileRepository
    ): GetUserProfileUseCase {
        return GetUserProfileUseCase(userProfileRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateUserProfileUseCase(
        userProfileRepository: UserProfileRepository
    ): UpdateUserProfileUseCase {
        return UpdateUserProfileUseCase(userProfileRepository)
    }

    @Provides
    @Singleton
    fun provideSignOutUseCase(
        userProfileRepository: UserProfileRepository
    ): SignOutUseCase {
        return SignOutUseCase(userProfileRepository)
    }
}
