package com.example.giga_chat_pet.data.parser

import com.example.giga_chat_pet.domain.MarkdownPreprocessor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LatexPreprocessor @Inject constructor() : MarkdownPreprocessor {

    override fun preprocess(text: String): String {
        return text
            .replace(Regex("\\$(.+?)\\$"), "`$1`")
            .replace(Regex("\\$\\$(.+?)\\$\\$", RegexOption.DOT_MATCHES_ALL), "```\n$1\n```")
    }
}
