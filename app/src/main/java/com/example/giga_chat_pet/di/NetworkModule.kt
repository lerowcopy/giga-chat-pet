package com.example.giga_chat_pet.di

import android.content.Context
import com.example.giga_chat_pet.BuildConfig
import com.example.giga_chat_pet.data.remote.TokenApi
import com.example.giga_chat_pet.data.remote.TokenManager
import com.example.giga_chat_pet.data.remote.buildSslOkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideGigaChatApi(
        @ApplicationContext context: Context,
        tokenManager: TokenManager
    ): com.example.giga_chat_pet.data.remote.GigaChatApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = okhttp3.Interceptor { chain ->
            val token = kotlinx.coroutines.runBlocking { tokenManager.getValidToken() }
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
            .create(com.example.giga_chat_pet.data.remote.GigaChatApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenApi(@ApplicationContext context: Context): TokenApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = buildSslOkHttpClient(context)
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://ngw.devices.sberbank.ru:9443/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TokenApi::class.java)
    }
}
