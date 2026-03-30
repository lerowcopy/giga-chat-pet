package com.example.giga_chat_pet.data.remote

import android.content.Context
import android.util.Log
import com.example.giga_chat_pet.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

class TokenManager(context: Context) {

    private val tokenApi: TokenApi

    private var cachedToken: String? = null
    // expiresAt приходит в миллисекундах Unix timestamp
    private var expiresAt: Long = 0L

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = buildSslOkHttpClient(context)
            .addInterceptor(loggingInterceptor)
            .build()

        tokenApi = Retrofit.Builder()
            .baseUrl("https://ngw.devices.sberbank.ru:9443/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TokenApi::class.java)
    }

    suspend fun getValidToken(): String {
        val now = System.currentTimeMillis()
        // Обновляем токен за 60 секунд до истечения
        if (cachedToken != null && now < expiresAt - 60_000) {
            return cachedToken!!
        }
        return refreshToken()
    }

    private suspend fun refreshToken(): String {
        val response = tokenApi.getToken(
            authKey = "Basic ${BuildConfig.GIGACHAT_AUTH_KEY}",
            rqUid = UUID.randomUUID().toString(),
            scope = BuildConfig.GIGACHAT_SCOPE
        )

        val body = response.body()
        if (!response.isSuccessful || body == null) {
            val error = response.errorBody()?.string()
            Log.e("TokenManager", "Failed to get token: $error")
            error("Failed to get GigaChat token: ${response.code()} $error")
        }

        cachedToken = body.accessToken
        expiresAt = body.expiresAt
        Log.d("TokenManager", "Token refreshed, expires at $expiresAt")
        return body.accessToken
    }
}
