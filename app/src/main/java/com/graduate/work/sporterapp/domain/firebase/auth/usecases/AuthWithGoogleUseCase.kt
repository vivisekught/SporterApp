package com.graduate.work.sporterapp.domain.firebase.auth.usecases

import androidx.credentials.GetCredentialResponse
import com.google.firebase.auth.AuthResult
import com.graduate.work.sporterapp.domain.firebase.auth.repositories.FirebaseAuthRepository
import com.graduate.work.sporterapp.features.login.core.AuthResponse
import javax.inject.Inject

class AuthWithGoogleUseCase @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
) {
    suspend operator fun invoke(credentialAuthResponse: AuthResponse<GetCredentialResponse>): AuthResponse<AuthResult> {
        if (credentialAuthResponse is AuthResponse.Failure) {
            return AuthResponse.Failure(credentialAuthResponse.message)
        }
        val credential =
            (credentialAuthResponse as AuthResponse.Success<GetCredentialResponse>).data ?: run {
                return AuthResponse.Failure("Check your internet connection")
            }
        return authRepository.authWithGoogle(credential)
    }
}