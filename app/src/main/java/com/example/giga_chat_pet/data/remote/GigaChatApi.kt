package com.example.giga_chat_pet.data.remote

import android.content.Context
import com.example.giga_chat_pet.BuildConfig
import com.example.giga_chat_pet.data.model.ChatRequest
import com.example.giga_chat_pet.data.model.ChatResponse
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface GigaChatApi {

    @POST("api/v1/chat/completions")
    suspend fun sendMessage(@Body request: ChatRequest): Response<ChatResponse>

    companion object {
        fun create(context: Context): GigaChatApi {
            val tokenManager = TokenManager(context)

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Interceptor получает свежий токен перед каждым запросом
            val authInterceptor = Interceptor { chain ->
                val token = runBlocking { tokenManager.getValidToken() }
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }

            val client = buildSslOkHttpClient(context)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl("https://gigachat.devices.sberbank.ru/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GigaChatApi::class.java)
        }
    }
}
