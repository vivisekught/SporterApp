package com.graduate.work.sporterapp.features.login.screens.sign_in.screen

import android.content.Context
import kotlinx.coroutines.CoroutineScope

sealed class SignInScreenEvent {

    // Navigation
    data object NavigateToForgetPasswordScreen : SignInScreenEvent()
    data object NavigateToSignUpScreen : SignInScreenEvent()
    data object NavigateToHomeScreen : SignInScreenEvent()
    data object NavigateToEmailVerificationScreen : SignInScreenEvent()

    // Auth
    data object SignInWithEmailAndPassword : SignInScreenEvent()
    data class AuthWithGoogle(val scope: CoroutineScope, val context: Context) : SignInScreenEvent()

    // Inputs
    data class OnEmailChanged(val email: String) : SignInScreenEvent()
    data class OnPasswordChanged(val password: String) : SignInScreenEvent()

    // Errors
    data object ResetGoogleAuthErrorState : SignInScreenEvent()
}