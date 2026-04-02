package com.example.giga_chat_pet.data.api

import com.example.giga_chat_pet.data.model.ChatRequest
import com.example.giga_chat_pet.data.model.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApiService {

    @POST("api/v1/chat/completions")
    suspend fun sendMessage(@Body request: ChatRequest): Response<ChatResponse>
}
