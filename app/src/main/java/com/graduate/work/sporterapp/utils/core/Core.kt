package com.graduate.work.sporterapp.utils.core

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.graduate.work.sporterapp.BuildConfig
import com.graduate.work.sporterapp.utils.core.auth.Response
import java.security.MessageDigest
import java.util.UUID

object Core {
    @Composable
    inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
        val navGraphRoute = destination.parent?.route ?: return viewModel()
        val parentEntry = remember(this) {
            navController.getBackStackEntry(navGraphRoute)
        }
        return viewModel(parentEntry)
    }

    suspend fun Context.getGoogleCredential(): Response<GetCredentialResponse> {
        val request = getGoogleRequest()
        val credentialManager = CredentialManager.create(this)
        return try {
            val result = credentialManager.getCredential(
                context = this,
                request = request
            )
            Response.Success(result)
        } catch (e: Exception) {
            Log.d("AAAAAA", "getGoogleCredential: ${e.message}")
            Response.Failure("Something went wrong, please try later")
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