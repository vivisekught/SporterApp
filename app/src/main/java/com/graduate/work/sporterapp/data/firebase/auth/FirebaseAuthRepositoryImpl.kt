package com.graduate.work.sporterapp.data.firebase.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.graduate.work.sporterapp.BuildConfig
import com.graduate.work.sporterapp.domain.firebase.auth.repositories.FirebaseAuthRepository
import com.graduate.work.sporterapp.utils.core.auth.Response
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class FirebaseAuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
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

    override suspend fun signInWithGoogle(): Response<AuthResult> {
        val request = getGoogleRequest()
        val credentialManager = CredentialManager.create(context)
        try {
            val result = credentialManager.getCredential(
                context = context,
                request = request
            )
            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(result.credential.data)
            val googleIdToken = googleIdTokenCredential.idToken
            val firebaseCredential =
                GoogleAuthProvider.getCredential(googleIdToken, null)
            return suspendCoroutine { continuation ->
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(Response.Success(task.result))
                        } else {
                            continuation.resume(Response.Failure(task.exception?.message.toString()))
                        }
                    }
            }

        } catch (e: GoogleIdTokenParsingException) {
            return Response.Failure(e.message.toString())
        } catch (e: Exception) {
            return Response.Failure(e.message.toString())
        }
    }

    override suspend fun signUpWithMailAndPassword(
        email: String,
        password: String,
    ): Response<AuthResult> {
        return suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Response.Success(task.result))
                    } else {
                        continuation.resume(Response.Failure(task.exception?.message.toString()))
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
                        continuation.resume(Response.Failure(task.exception?.message.toString()))
                    }
                }
        }
    }

    private fun getGoogleRequest(): GetCredentialRequest {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(bytes)
        val rawHash = hash.fold("") { str, it -> str + "%02x".format(it) }
        val googleIdOptions = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.WEB_API_KEY)
            .setNonce(rawHash)
            .build()
        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOptions)
            .build()
    }
}