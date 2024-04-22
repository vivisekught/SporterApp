package com.graduate.work.sporterapp.features.login.screens.sign_in

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.graduate.work.sporterapp.features.login.screens.sign_in.screen.SignInScreen
import com.graduate.work.sporterapp.features.login.screens.sign_in.screen.SignInScreenEvent
import com.graduate.work.sporterapp.features.login.screens.sign_in.vm.SignInViewModel
import com.graduate.work.sporterapp.navigation.AppNavigation
import com.graduate.work.sporterapp.utils.core.Core.getGoogleCredential
import kotlinx.coroutines.launch

@Composable
fun SignInCompleteScreen(navController: NavController) {
    val viewModel = hiltViewModel<SignInViewModel>()
    SignInScreen(viewModel.uiState) { eventState ->
        when (eventState) {
            SignInScreenEvent.NavigateToForgetPassword -> {
                navController.navigate(AppNavigation.Auth.ForgetPasswordScreen.route)
            }

            SignInScreenEvent.NavigateToHomeScreen -> {
                Log.d("AAAAAA", "NavigateToHomeScreen")
            }

            SignInScreenEvent.NavigateToOnBoarding -> {
                Log.d("AAAAAA", "NavigateToOnBoarding")
            }

            SignInScreenEvent.NavigateToSignUp -> {
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