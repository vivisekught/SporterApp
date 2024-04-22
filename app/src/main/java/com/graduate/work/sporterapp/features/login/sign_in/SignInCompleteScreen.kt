package com.graduate.work.sporterapp.features.login.sign_in

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.graduate.work.sporterapp.features.login.sign_in.screen.SignInScreen
import com.graduate.work.sporterapp.features.login.sign_in.screen.SignInScreenEvent
import com.graduate.work.sporterapp.features.login.sign_in.vm.SignInViewModel
import com.graduate.work.sporterapp.navigation.AppNavigation

@Composable
fun SignInCompleteScreen(navController: NavController) {
    val signInViewModel = hiltViewModel<SignInViewModel>()
    SignInScreen(signInViewModel.uiState) { eventState ->
        when (eventState) {
            SignInScreenEvent.NavigateToForgetPassword -> {
                navController.navigate(AppNavigation.Auth.ForgotPasswordScreen.route)
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
                signInViewModel.onEmailChange(eventState.email)
            }

            is SignInScreenEvent.OnPasswordChanged -> {
                signInViewModel.onPasswordChange(eventState.password)
            }

            SignInScreenEvent.SignInWithEmailAndPassword -> {
                signInViewModel.signInWithEmailAndPassword()
            }

            SignInScreenEvent.SignInWithGoogle -> {
                signInViewModel.signInWithGoogle()
            }
        }
    }

}