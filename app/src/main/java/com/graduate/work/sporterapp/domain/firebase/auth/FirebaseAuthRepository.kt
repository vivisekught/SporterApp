package com.graduate.work.sporterapp.domain.firebase.auth

import androidx.credentials.GetCredentialResponse
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.graduate.work.sporterapp.core.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface FirebaseAuthRepository {
    fun getAuthState(scope: CoroutineScope): StateFlow<FirebaseUser?>

    suspend fun authWithGoogle(credential: GetCredentialResponse): Response<AuthResult>

    suspend fun signUpWithMailAndPassword(email: String, password: String): Response<Unit>

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Response<AuthResult>

    suspend fun sendPasswordResetEmail(email: String): Response<Unit>

    fun getUserId(): String?
}