package com.graduate.work.sporterapp.domain.firebase.auth.usecases

import com.graduate.work.sporterapp.domain.firebase.auth.repositories.FirebaseAuthRepository
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository,
) {
    suspend operator fun invoke(email: String) =
        firebaseAuthRepository.sendPasswordResetEmail(email)
}