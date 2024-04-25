package com.graduate.work.sporterapp.features.login.screens.sign_up

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.graduate.work.sporterapp.features.login.screens.sign_up.screen.SignUpScreen
import com.graduate.work.sporterapp.features.login.screens.sign_up.screen.SignUpScreenEvent
import com.graduate.work.sporterapp.features.login.screens.sign_up.vm.SignUpViewModel
import com.graduate.work.sporterapp.navigation.AppNavigation
import com.graduate.work.sporterapp.utils.core.Core.getGoogleCredential
import com.graduate.work.sporterapp.utils.core.Core.openLinkInWebBrowser
import kotlinx.coroutines.launch

@Composable
fun SignUpCompleteScreen(navController: NavController) {
    val viewModel: SignUpViewModel = hiltViewModel<SignUpViewModel>()
    val context = LocalContext.current
    SignUpScreen(uiState = viewModel.uiState) { eventState ->
        when (eventState) {
            is SignUpScreenEvent.AuthWithGoogle -> {
                viewModel.setLoading()
                eventState.scope.launch {
                    val credentialResponse = eventState.context.getGoogleCredential()
                    viewModel.authWithGoogle(credentialResponse)
                }
            }

            SignUpScreenEvent.NavigateToEmailVerification -> {
                navController.navigate(AppNavigation.Auth.EmailVerificationScreen.route)
            }

            SignUpScreenEvent.NavigateToHome -> {
                Log.d("AAAAAA", "NavigateToHome")
            }

            SignUpScreenEvent.NavigateToOnBoarding -> {
                Log.d("AAAAAA", "NavigateToOnBoarding")
            }

            SignUpScreenEvent.NavigateToSignIn ->
                navController.navigate(AppNavigation.Auth.SignInScreen.route)

            is SignUpScreenEvent.OnEmailChanged -> viewModel.onEmailChange(eventState.email)
            is SignUpScreenEvent.OnPasswordChanged -> viewModel.onPasswordChange(eventState.password)
            is SignUpScreenEvent.OnUserNameChanged -> viewModel.onUserNameChange(eventState.userName)
            SignUpScreenEvent.SignUpWithEmailAndPassword -> viewModel.signUpWithMailAndPassword()
            SignUpScreenEvent.ResetGoogleAuthErrorState -> viewModel.resetErrors()
            SignUpScreenEvent.OpenPolicy -> {
                openLinkInWebBrowser(
                    context,
                    "https://sites.google.com/view/sporterapppolicy/policy"
                )
            }

            SignUpScreenEvent.OpenTerms -> {
                openLinkInWebBrowser(context, "https://sites.google.com/view/sporterappterms/terms")
            }

            is SignUpScreenEvent.OnPolicyAndTermsChanged -> {
                viewModel.onAgreePolicy(eventState.isAgree)
            }
        }
    }
}