package com.graduate.work.sporterapp.domain.firebase.auth.repositories

import androidx.credentials.GetCredentialResponse
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.graduate.work.sporterapp.features.login.core.AuthResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface FirebaseAuthRepository {
    fun getAuthState(scope: CoroutineScope): StateFlow<FirebaseUser?>

    suspend fun authWithGoogle(credential: GetCredentialResponse): AuthResponse<AuthResult>

    suspend fun signUpWithMailAndPassword(email: String, password: String): AuthResponse<Unit>

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthResponse<AuthResult>

    suspend fun sendPasswordResetEmail(email: String): AuthResponse<Unit>
}