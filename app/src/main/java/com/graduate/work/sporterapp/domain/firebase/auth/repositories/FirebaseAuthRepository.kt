package com.graduate.work.sporterapp.domain.firebase.auth.repositories

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.graduate.work.sporterapp.utils.core.auth.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface FirebaseAuthRepository {
    fun getAuthState(scope: CoroutineScope): StateFlow<FirebaseUser?>

    suspend fun signInWithGoogle(): Response<AuthResult>

    suspend fun signUpWithMailAndPassword(email: String, password: String): Response<AuthResult>

    suspend fun signInWithEmailAndPassword(email: String, password: String): Response<AuthResult>
}