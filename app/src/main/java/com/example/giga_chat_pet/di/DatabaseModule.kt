package com.example.giga_chat_pet.di

import android.content.Context
import com.example.giga_chat_pet.data.local.ChatDatabase
import com.example.giga_chat_pet.data.local.ConversationDao
import com.example.giga_chat_pet.data.local.MessageDao
import com.example.giga_chat_pet.data.storage.MessageStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideChatDatabase(@ApplicationContext context: Context): ChatDatabase {
        return ChatDatabase.getDatabase(context)
    }

    @Provides
    fun provideMessageDao(database: ChatDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    fun provideConversationDao(database: ChatDatabase): ConversationDao {
        return database.conversationDao()
    }

    @Provides
    @Singleton
    fun provideMessageStorage(messageDao: MessageDao): MessageStorage {
        return MessageStorage(messageDao)
    }
}
