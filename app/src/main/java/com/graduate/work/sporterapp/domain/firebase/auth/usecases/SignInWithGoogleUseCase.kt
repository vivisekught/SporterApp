package com.graduate.work.sporterapp.domain.firebase.auth.usecases

import com.graduate.work.sporterapp.domain.firebase.auth.repositories.FirebaseAuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
) {
    suspend operator fun invoke() = authRepository.signInWithGoogle()
}