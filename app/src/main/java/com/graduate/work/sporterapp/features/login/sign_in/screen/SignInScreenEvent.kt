package com.graduate.work.sporterapp.features.login.sign_in.screen

sealed class SignInScreenEvent {
    data object NavigateToForgetPassword : SignInScreenEvent()
    data object NavigateToSignUp : SignInScreenEvent()
    data object NavigateToHomeScreen : SignInScreenEvent()
    data object NavigateToOnBoarding : SignInScreenEvent()
    data object SignInWithEmailAndPassword : SignInScreenEvent()
    data object SignInWithGoogle : SignInScreenEvent()
    data class OnEmailChanged(val email: String) : SignInScreenEvent()
    data class OnPasswordChanged(val password: String) : SignInScreenEvent()
}