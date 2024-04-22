package com.graduate.work.sporterapp.features.login.screens.sign_up

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.graduate.work.sporterapp.features.login.screens.sign_up.screen.SignUpScreen
import com.graduate.work.sporterapp.features.login.screens.sign_up.screen.SignUpScreenEvent
import com.graduate.work.sporterapp.features.login.screens.sign_up.vm.SignUpViewModel
import com.graduate.work.sporterapp.navigation.AppNavigation
import com.graduate.work.sporterapp.utils.core.Core.getGoogleCredential
import kotlinx.coroutines.launch

@Composable
fun SignUpCompleteScreen(navController: NavController) {
    val viewModel: SignUpViewModel = hiltViewModel<SignUpViewModel>()
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
                Log.d("AAAAAA", "NavigateToEmailVerification")
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
            SignUpScreenEvent.ResetGoogleAuthErrorState -> viewModel.resetErrorGoogleAuthState()
        }
    }
}