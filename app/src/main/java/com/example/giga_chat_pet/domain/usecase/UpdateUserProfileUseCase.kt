package com.example.giga_chat_pet.domain.usecase

import com.example.giga_chat_pet.domain.repository.UserProfileRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(displayName: String) {
        userProfileRepository.updateDisplayName(displayName)
    }
}
