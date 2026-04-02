package com.example.giga_chat_pet.di

import com.example.giga_chat_pet.data.parser.LatexPreprocessor
import com.example.giga_chat_pet.domain.MarkdownPreprocessor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ParserModule {

    @Binds
    @Singleton
    abstract fun bindMarkdownPreprocessor(
        impl: LatexPreprocessor
    ): MarkdownPreprocessor
}
