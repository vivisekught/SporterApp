package com.graduate.work.sporterapp.domain.firebase.auth.usecases

import com.graduate.work.sporterapp.domain.firebase.auth.FirebaseAuthRepository
import javax.inject.Inject

class SignInAnonymouslyUseCase @Inject constructor(
    private val authRepository: FirebaseAuthRepository
) {

    suspend operator fun invoke() = authRepository.signInAnonymously()
}