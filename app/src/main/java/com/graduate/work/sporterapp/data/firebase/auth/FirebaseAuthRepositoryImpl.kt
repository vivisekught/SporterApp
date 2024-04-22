package com.graduate.work.sporterapp.data.firebase.auth

import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.graduate.work.sporterapp.domain.firebase.auth.repositories.FirebaseAuthRepository
import com.graduate.work.sporterapp.utils.core.auth.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


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

    override suspend fun authWithGoogle(credential: GetCredentialResponse): Response<AuthResult> {
        try {
            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(credential.credential.data)
            val googleIdToken = googleIdTokenCredential.idToken
            val firebaseCredential =
                GoogleAuthProvider.getCredential(googleIdToken, null)
            return suspendCoroutine { continuation ->
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(Response.Success(task.result))
                        } else {
                            continuation.resume(Response.Failure(task.exception?.localizedMessage.toString()))
                        }
                    }
            }

        } catch (e: Exception) {
            return Response.Failure("Unknown error")
        }
    }

    override suspend fun signUpWithMailAndPassword(
        email: String,
        password: String,
    ): Response<AuthResult> {
        return suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Response.Success(task.result))
                    } else {
                        continuation.resume(Response.Failure(task.exception?.localizedMessage.toString()))
                    }
                }
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Response<AuthResult> {
        return suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Response.Success(task.result))
                    } else {
                        continuation.resume(Response.Failure(task.exception?.localizedMessage.toString()))
                    }
                }
        }
    }
}