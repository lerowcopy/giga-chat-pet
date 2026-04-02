package com.example.giga_chat_pet.domain

interface MarkdownParser {
    fun parse(text: String): String
}
