package com.graduate.work.sporterapp.data.firebase.auth

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.graduate.work.sporterapp.domain.firebase.auth.FirebaseAuthRepository
import com.graduate.work.sporterapp.utils.helpers.auth.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
) : FirebaseAuthRepository {

    override fun getAuthState(scope: CoroutineScope): StateFlow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }.stateIn(scope, SharingStarted.WhileSubscribed(), auth.currentUser)

    override suspend fun signInAnonymously(): Response<AuthResult> {
        return try {
            val authResult = auth.signInAnonymously().await()
            Response.Success(authResult)
        } catch (error: Exception) {
            Response.Failure(error)
        }
    }
}