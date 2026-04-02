package com.example.giga_chat_pet.data.repository

import com.example.giga_chat_pet.data.local.UserProfileDao
import com.example.giga_chat_pet.data.local.UserProfileData
import com.example.giga_chat_pet.domain.model.UserProfile
import com.example.giga_chat_pet.domain.repository.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val firebaseAuth: FirebaseAuth
) : UserProfileRepository {

    override fun getUserProfile(): Flow<UserProfile?> {
        return userProfileDao.getUserProfile().map { it?.toDomain() }
    }

    override suspend fun updateDisplayName(displayName: String) {
        val user = firebaseAuth.currentUser ?: throw Exception("Пользователь не авторизован")

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()

        user.updateProfile(profileUpdates).await()

        val userProfileData = UserProfileData(
            uid = user.uid,
            email = user.email ?: "",
            displayName = user.displayName,
            photoUrl = user.photoUrl?.toString(),
            createdAt = user.metadata?.creationTimestamp ?: System.currentTimeMillis()
        )
        userProfileDao.insertUserProfile(userProfileData)
    }

    override suspend fun signOut() {
        userProfileDao.deleteUserProfile()
        firebaseAuth.signOut()
    }

    private fun UserProfileData.toDomain(): UserProfile {
        return UserProfile(
            uid = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoUrl,
            createdAt = createdAt
        )
    }
}
