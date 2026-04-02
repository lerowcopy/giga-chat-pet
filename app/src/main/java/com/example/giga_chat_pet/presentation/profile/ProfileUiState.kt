package com.example.giga_chat_pet.presentation.profile

data class ProfileUiState(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEditing: Boolean = false,
    val signOutSuccess: Boolean = false
)
