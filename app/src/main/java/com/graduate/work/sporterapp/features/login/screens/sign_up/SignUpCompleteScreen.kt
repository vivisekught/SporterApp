package com.graduate.work.sporterapp.features.login.screens.sign_up

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.graduate.work.sporterapp.features.login.screens.sign_up.screen.SignUpScreen
import com.graduate.work.sporterapp.features.login.screens.sign_up.screen.SignUpScreenEvent
import com.graduate.work.sporterapp.features.login.screens.sign_up.vm.SignUpViewModel
import com.graduate.work.sporterapp.features.login.utils.LoginUtils.POLICY_LINK
import com.graduate.work.sporterapp.features.login.utils.LoginUtils.TERMS_LINK
import com.graduate.work.sporterapp.features.login.utils.LoginUtils.getGoogleCredential
import com.graduate.work.sporterapp.features.login.utils.LoginUtils.openLinkInWebBrowser
import com.graduate.work.sporterapp.navigation.AppNavigation
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
                // TODO: navigate to home
            }

            SignUpScreenEvent.NavigateToSignIn ->
                navController.navigate(AppNavigation.Auth.SignInScreen.route)

            is SignUpScreenEvent.OnEmailChanged -> viewModel.onEmailChange(eventState.email)
            is SignUpScreenEvent.OnPasswordChanged -> viewModel.onPasswordChange(eventState.password)
            is SignUpScreenEvent.OnUserNameChanged -> viewModel.onUserNameChange(eventState.userName)
            SignUpScreenEvent.SignUpWithEmailAndPassword -> viewModel.signUpWithMailAndPassword()
            SignUpScreenEvent.ResetGoogleAuthErrorState -> viewModel.resetErrors()
            SignUpScreenEvent.OpenPolicy -> {
                openLinkInWebBrowser(context, POLICY_LINK)
            }

            SignUpScreenEvent.OpenTerms -> {
                openLinkInWebBrowser(context, TERMS_LINK)
            }

            is SignUpScreenEvent.OnPolicyAndTermsChanged -> {
                viewModel.onAgreePolicy(eventState.isAgree)
            }
        }
    }
}