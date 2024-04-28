package com.graduate.work.sporterapp.features.login.screens.forget_password

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.graduate.work.sporterapp.features.login.screens.forget_password.screen.ForgetPasswordScreen
import com.graduate.work.sporterapp.features.login.screens.forget_password.screen.ForgetPasswordScreenEvent
import com.graduate.work.sporterapp.features.login.screens.forget_password.vm.ForgetPasswordViewModel
import com.graduate.work.sporterapp.navigation.AppNavigation

@Composable
fun ForgetPasswordCompleteScreen(navController: NavController) {
    val viewModel: ForgetPasswordViewModel = hiltViewModel<ForgetPasswordViewModel>()
    ForgetPasswordScreen(uiState = viewModel.uiState) {
        when (it) {
            ForgetPasswordScreenEvent.NavigateToSignInScreen -> {
                navController.navigate(AppNavigation.Auth.SignInScreen.route)
            }

            is ForgetPasswordScreenEvent.OnEmailChanged -> {
                viewModel.onEmailChanged(it.email)
            }

            ForgetPasswordScreenEvent.OnSendEmailClick -> {
                viewModel.sendPasswordResetEmail()
            }
        }
    }
}