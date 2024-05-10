package com.graduate.work.sporterapp.domain.firebase.auth.usecases

import com.graduate.work.sporterapp.domain.firebase.auth.FirebaseAuthRepository
import javax.inject.Inject

class GetUserIdUseCase @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
) {
    operator fun invoke(): String? = authRepository.getUserId()
}