package com.example.giga_chat_pet.domain.model

data class UserProfile(
    val uid: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val createdAt: Long
)
