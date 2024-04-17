package com.graduate.work.sporterapp.domain.firebase.auth

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.graduate.work.sporterapp.utils.helpers.auth.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface FirebaseAuthRepository {
    fun getAuthState(scope: CoroutineScope): StateFlow<FirebaseUser?>

    suspend fun signInAnonymously(): Response<AuthResult>
}