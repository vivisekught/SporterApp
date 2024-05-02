package com.graduate.work.sporterapp.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.graduate.work.sporterapp.core.ui.theme.AppTheme
import com.graduate.work.sporterapp.features.maps.screen.HomeMapScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val authState = viewModel.currentAuthState.collectAsState().value
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeMapScreen()
//                    val navController = rememberNavController()
//                    NavHost(
//                        navController = navController,
//                        startDestination = AppNavigation.Auth.AUTH_FEATURE_SCREEN_ROUTE
//                    ) {
//                        navigation(
//                            startDestination = AppNavigation.Auth.SignInScreen.route,
//                            route = AppNavigation.Auth.AUTH_FEATURE_SCREEN_ROUTE,
//                        ) {
//                            composable(AppNavigation.Auth.SignInScreen.route) {
//                                SignInCompleteScreen(
//                                    email = authState?.email,
//                                    navController = navController
//                                )
//                            }
//                            composable(AppNavigation.Auth.SignUpScreen.route) {
//                                SignUpCompleteScreen(navController = navController)
//                            }
//                            composable(AppNavigation.Auth.ForgetPasswordScreen.route) {
//                                ForgetPasswordCompleteScreen(navController = navController)
//                            }
//                            composable(AppNavigation.Auth.EmailVerificationScreen.route) {
//                                EmailVerificationCompleteScreen(
//                                    email = authState?.email ?: "",
//                                    navController = navController,
//                                )
//                            }
//                        }
//                    }
                }
            }
        }
    }
}