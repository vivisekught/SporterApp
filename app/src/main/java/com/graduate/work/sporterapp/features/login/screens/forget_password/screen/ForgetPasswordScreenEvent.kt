package com.graduate.work.sporterapp.features.login.screens.forget_password.screen

sealed class ForgetPasswordScreenEvent {
    data object NavigateToSignInScreen : ForgetPasswordScreenEvent()
    data object OnSendEmailClick : ForgetPasswordScreenEvent()
    data class OnEmailChanged(val email: String) : ForgetPasswordScreenEvent()
}