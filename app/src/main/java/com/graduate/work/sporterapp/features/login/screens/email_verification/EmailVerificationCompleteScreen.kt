package com.graduate.work.sporterapp.features.login.screens.email_verification

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.graduate.work.sporterapp.features.login.screens.email_verification.screen.EmailVerificationScreen
import com.graduate.work.sporterapp.navigation.AppNavigation

@Composable
fun EmailVerificationCompleteScreen(
    email: String,
    navController: NavController,
) {
    EmailVerificationScreen(email) {
        navController.navigate(AppNavigation.Auth.SignInScreen.route, navOptions = navOptions {
            popUpTo(AppNavigation.Auth.SignInScreen.route) { inclusive = true }
        })
    }
}