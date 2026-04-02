package com.example.giga_chat_pet.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileData(
    @PrimaryKey
    val uid: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val createdAt: Long
)
