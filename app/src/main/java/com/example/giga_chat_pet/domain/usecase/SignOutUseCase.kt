package com.example.giga_chat_pet.domain.usecase

import com.example.giga_chat_pet.domain.repository.UserProfileRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke() {
        userProfileRepository.signOut()
    }
}
