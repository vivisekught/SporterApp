package com.graduate.work.sporterapp.features.login.screens.sign_up.screen

import android.content.Context
import kotlinx.coroutines.CoroutineScope

sealed class SignUpScreenEvent {
    data object NavigateToSignIn : SignUpScreenEvent()
    data object NavigateToEmailVerification : SignUpScreenEvent()
    data object NavigateToHome : SignUpScreenEvent()
    data object NavigateToOnBoarding : SignUpScreenEvent()
    data object SignUpWithEmailAndPassword : SignUpScreenEvent()
    data class AuthWithGoogle(val scope: CoroutineScope, val context: Context) : SignUpScreenEvent()
    data class OnUserNameChanged(val userName: String) : SignUpScreenEvent()
    data class OnEmailChanged(val email: String) : SignUpScreenEvent()
    data class OnPasswordChanged(val password: String) : SignUpScreenEvent()
    data object ResetGoogleAuthErrorState : SignUpScreenEvent()
    data object OpenPolicy : SignUpScreenEvent()
    data object OpenTerms : SignUpScreenEvent()
    data class OnPolicyAndTermsChanged(val isAgree: Boolean) : SignUpScreenEvent()
}