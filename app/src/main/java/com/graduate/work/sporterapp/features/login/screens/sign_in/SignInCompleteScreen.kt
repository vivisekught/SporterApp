package com.graduate.work.sporterapp.features.login.screens.sign_in

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.graduate.work.sporterapp.features.login.screens.sign_in.screen.SignInScreen
import com.graduate.work.sporterapp.features.login.screens.sign_in.screen.SignInScreenEvent
import com.graduate.work.sporterapp.features.login.screens.sign_in.vm.SignInViewModel
import com.graduate.work.sporterapp.navigation.AppNavigation
import com.graduate.work.sporterapp.utils.core.Core.getGoogleCredential
import kotlinx.coroutines.launch

@Composable
fun SignInCompleteScreen(email: String?, navController: NavController) {
    val viewModel = hiltViewModel<SignInViewModel>()
    SignInScreen(email, viewModel.uiState) { eventState ->
        when (eventState) {
            SignInScreenEvent.NavigateToForgetPasswordScreen -> {
                navController.navigate(AppNavigation.Auth.ForgetPasswordScreen.route)
            }

            SignInScreenEvent.NavigateToHomeScreen -> {
            }

            SignInScreenEvent.NavigateToEmailVerificationScreen -> {
                navController.navigate(
                    AppNavigation.Auth.EmailVerificationScreen.route,
                    navOptions = navOptions {
                        popUpTo(AppNavigation.Auth.SignInScreen.route) { inclusive = true }
                    })
            }

            SignInScreenEvent.NavigateToSignUpScreen -> {
                navController.navigate(AppNavigation.Auth.SignUpScreen.route)
            }

            is SignInScreenEvent.OnEmailChanged -> {
                viewModel.onEmailChange(eventState.email)
            }

            is SignInScreenEvent.OnPasswordChanged -> {
                viewModel.onPasswordChange(eventState.password)
            }

            SignInScreenEvent.SignInWithEmailAndPassword -> {
                viewModel.signInWithEmailAndPassword()
            }

            is SignInScreenEvent.AuthWithGoogle -> {
                viewModel.setLoading()
                eventState.scope.launch {
                    val credentialResponse = eventState.context.getGoogleCredential()
                    viewModel.authWithGoogle(credentialResponse)
                }
            }

            SignInScreenEvent.ResetGoogleAuthErrorState -> viewModel.resetErrorGoogleAuthState()
        }
    }

}