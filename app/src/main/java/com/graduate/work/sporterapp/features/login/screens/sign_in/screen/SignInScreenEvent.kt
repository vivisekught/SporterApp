package com.graduate.work.sporterapp.features.login.screens.sign_in.screen

import android.content.Context
import kotlinx.coroutines.CoroutineScope

sealed class SignInScreenEvent {
    data object NavigateToForgetPassword : SignInScreenEvent()
    data object NavigateToSignUp : SignInScreenEvent()
    data object NavigateToHomeScreen : SignInScreenEvent()
    data object NavigateToOnBoarding : SignInScreenEvent()
    data object SignInWithEmailAndPassword : SignInScreenEvent()
    data class AuthWithGoogle(val scope: CoroutineScope, val context: Context) : SignInScreenEvent()
    data class OnEmailChanged(val email: String) : SignInScreenEvent()
    data class OnPasswordChanged(val password: String) : SignInScreenEvent()
    data object ResetGoogleAuthErrorState : SignInScreenEvent()
}