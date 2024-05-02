package com.graduate.work.sporterapp.domain.firebase.auth.usecases

import com.graduate.work.sporterapp.domain.firebase.auth.FirebaseAuthRepository
import javax.inject.Inject

class SignInWithEmailAndPasswordUseCase @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
) {

    suspend operator fun invoke(email: String, password: String) =
        authRepository.signInWithEmailAndPassword(email, password)
}