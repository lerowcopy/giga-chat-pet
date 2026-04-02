package com.example.giga_chat_pet.domain

interface MarkdownPreprocessor {
    fun preprocess(text: String): String
}
