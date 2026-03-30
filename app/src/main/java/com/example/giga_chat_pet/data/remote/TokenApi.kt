package com.example.giga_chat_pet.data.remote

import com.example.giga_chat_pet.data.model.TokenResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface TokenApi {
    @FormUrlEncoded
    @POST("api/v2/oauth")
    suspend fun getToken(
        @Header("Authorization") authKey: String,
        @Header("RqUID") rqUid: String,
        @Field("scope") scope: String
    ): Response<TokenResponse>
}
