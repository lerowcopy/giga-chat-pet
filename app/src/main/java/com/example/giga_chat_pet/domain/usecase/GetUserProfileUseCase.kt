package com.example.giga_chat_pet.domain.usecase

import com.example.giga_chat_pet.domain.model.UserProfile
import com.example.giga_chat_pet.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    operator fun invoke(): Flow<UserProfile?> {
        return userProfileRepository.getUserProfile()
    }
}
