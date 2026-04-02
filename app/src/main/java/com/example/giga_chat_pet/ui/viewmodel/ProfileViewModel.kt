package com.example.giga_chat_pet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.giga_chat_pet.domain.usecase.GetUserProfileUseCase
import com.example.giga_chat_pet.domain.usecase.SignOutUseCase
import com.example.giga_chat_pet.domain.usecase.UpdateUserProfileUseCase
import com.example.giga_chat_pet.presentation.profile.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getUserProfileUseCase().collect { profile ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        uid = profile?.uid ?: "",
                        email = profile?.email ?: "",
                        displayName = profile?.displayName ?: ""
                    )
                }
            }
        }
    }

    fun updateDisplayName(displayName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                updateUserProfileUseCase(displayName)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        displayName = displayName,
                        isEditing = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка обновления имени"
                    )
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                signOutUseCase()
                _uiState.update { it.copy(isLoading = false, signOutSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка выхода"
                    )
                }
            }
        }
    }

    fun setEditing(isEditing: Boolean) {
        _uiState.update { it.copy(isEditing = isEditing) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetSignOutSuccess() {
        _uiState.update { it.copy(signOutSuccess = false) }
    }
}
