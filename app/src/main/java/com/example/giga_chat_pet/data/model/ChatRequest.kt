package com.example.giga_chat_pet.data.model

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("model")
    val model: String = "GigaChat",
    @SerializedName("messages")
    val messages: List<MessageDto>,
    @SerializedName("temperature")
    val temperature: Float = 0.7f
)

data class MessageDto(
    @SerializedName("role")
    val role: String,
    @SerializedName("content")
    val content: String
) {
    companion object {
        fun user(content: String) = MessageDto("user", content)
        fun assistant(content: String) = MessageDto("assistant", content)
    }
}

data class ChatResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("choices")
    val choices: List<Choice>,
    @SerializedName("created")
    val created: Long,
    @SerializedName("model")
    val model: String
)

data class Choice(
    @SerializedName("index")
    val index: Int,
    @SerializedName("message")
    val message: MessageDto,
    @SerializedName("finish_reason")
    val finishReason: String
)
