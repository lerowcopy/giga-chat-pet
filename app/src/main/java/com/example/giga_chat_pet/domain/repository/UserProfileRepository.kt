package com.example.giga_chat_pet.domain.repository

import com.example.giga_chat_pet.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun updateDisplayName(displayName: String)
    suspend fun signOut()
}
