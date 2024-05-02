package com.graduate.work.sporterapp.features.login.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.graduate.work.sporterapp.BuildConfig
import com.graduate.work.sporterapp.core.Response
import java.security.MessageDigest
import java.util.UUID

object LoginUtils {

    const val POLICY_LINK = "https://sites.google.com/view/sporterapppolicy/policy"
    const val TERMS_LINK = "https://sites.google.com/view/sporterappterms/terms"

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