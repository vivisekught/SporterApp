package com.graduate.work.sporterapp.domain.firebase.auth.usecases

import androidx.credentials.GetCredentialResponse
import com.google.firebase.auth.AuthResult
import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.domain.firebase.auth.FirebaseAuthRepository
import javax.inject.Inject

class AuthWithGoogleUseCase @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
) {
    suspend operator fun invoke(credentialResponse: Response<GetCredentialResponse>): Response<AuthResult> {
        if (credentialResponse is Response.Failure) {
            return Response.Failure(credentialResponse.message)
        }
        val credential =
            (credentialResponse as Response.Success<GetCredentialResponse>).data ?: run {
                return Response.Failure("Check your internet connection")
            }
        return authRepository.authWithGoogle(credential)
    }
}