package com.graduate.work.sporterapp.main

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.graduate.work.sporterapp.core.ui.theme.AppTheme
import com.graduate.work.sporterapp.features.home.HomeCompleteScreen
import com.graduate.work.sporterapp.features.login.screens.email_verification.EmailVerificationCompleteScreen
import com.graduate.work.sporterapp.features.login.screens.forget_password.ForgetPasswordCompleteScreen
import com.graduate.work.sporterapp.features.login.screens.sign_in.SignInCompleteScreen
import com.graduate.work.sporterapp.features.login.screens.sign_up.SignUpCompleteScreen
import com.graduate.work.sporterapp.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intentData: Uri? = intent?.data
        if (intentData != null) {
            val code: String? = intentData.getQueryParameter("code")
            if (code != null) {
                Log.d("Code from URI", code)
            } else {
                Log.d("Code from URI", "No 'code' parameter found in the URI")
            }
        }
        installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.isSplashLoading.value }
        }
        setContent {
            val authState = viewModel.currentAuthState.collectAsState().value
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination =
                        if (authState?.isEmailVerified == true) {
                            AppNavigation.Home.HOME_FEATURE_SCREEN_ROUTE
                        } else {
                            AppNavigation.Auth.AUTH_FEATURE_SCREEN_ROUTE
                        }
                    ) {
                        navigation(
                            startDestination = AppNavigation.Auth.SignInScreen.route,
                            route = AppNavigation.Auth.AUTH_FEATURE_SCREEN_ROUTE,
                        ) {
                            composable(AppNavigation.Auth.SignInScreen.route) {
                                SignInCompleteScreen(
                                    email = authState?.email,
                                    navController = navController
                                )
                            }
                            composable(AppNavigation.Auth.SignUpScreen.route) {
                                SignUpCompleteScreen(navController = navController)
                            }
                            composable(AppNavigation.Auth.ForgetPasswordScreen.route) {
                                ForgetPasswordCompleteScreen(navController = navController)
                            }
                            composable(AppNavigation.Auth.EmailVerificationScreen.route) {
                                EmailVerificationCompleteScreen(
                                    email = authState?.email ?: "",
                                    navController = navController,
                                )
                            }
                        }
                        composable(AppNavigation.Home.HOME_FEATURE_SCREEN_ROUTE) {
                            HomeCompleteScreen()
                        }
                    }
                }
            }
        }
    }
}