package com.graduate.work.sporterapp.utils.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
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

    fun openLinkInWebBrowser(context: Context, link: String) {
        val uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
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
        } catch (e: GetCredentialCancellationException) {
            Response.Failure("User cancelled the request")
        } catch (e: NoCredentialException) {
            Response.Failure("Credentials not found")
        } catch (e: GetCredentialException) {
            Response.Failure("Error fetching the credentials")
        } catch (e: Exception) {
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