package com.graduate.work.sporterapp.main

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions
import com.graduate.work.sporterapp.core.ui.theme.AppTheme
import com.graduate.work.sporterapp.data.maps.location.TrackingUserWorkoutService
import com.graduate.work.sporterapp.features.home.HomeCompleteScreen
import com.graduate.work.sporterapp.features.login.screens.email_verification.EmailVerificationCompleteScreen
import com.graduate.work.sporterapp.features.login.screens.forget_password.ForgetPasswordCompleteScreen
import com.graduate.work.sporterapp.features.login.screens.sign_in.SignInCompleteScreen
import com.graduate.work.sporterapp.features.login.screens.sign_up.SignUpCompleteScreen
import com.graduate.work.sporterapp.features.track.TrackCompleteScreen
import com.graduate.work.sporterapp.features.track.helper.TrackHelper.pendingUri
import com.graduate.work.sporterapp.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    private val launcher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    private var isTrackingServiceBound by mutableStateOf(false)
    private lateinit var trackingService: TrackingUserWorkoutService

    // Tracking service binding
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = p1 as TrackingUserWorkoutService.LocalBinder
            trackingService = binder.getService()
            isTrackingServiceBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isTrackingServiceBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, TrackingUserWorkoutService::class.java).also { intent ->
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.isSplashLoading.value }
        }
        requestNotificationPermission()
        setContent {
            if (isTrackingServiceBound) {
                val authState = viewModel.currentAuthState.collectAsState().value
                AppTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = if (authState?.isEmailVerified == true) {
                                AppNavigation.Home.HOME_FEATURE_SCREEN_ROUTE
                            } else {
                                if (trackingService.isWorkoutStarted) {
                                    AppNavigation.Workout.TRACK_FEATURE_SCREEN_ROUTE
                                } else {
                                    AppNavigation.Auth.AUTH_FEATURE_SCREEN_ROUTE
                                }
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
                            navigation(
                                route = AppNavigation.Workout.TRACK_FEATURE_SCREEN_ROUTE,
                                startDestination = AppNavigation.Workout.TrackScreen.route
                            ) {
                                composable(
                                    AppNavigation.Workout.TrackScreen.route,
                                    deepLinks = listOf(
                                        navDeepLink {
                                            uriPattern = pendingUri
                                        }
                                    ),
                                    arguments = listOf(
                                        navArgument(AppNavigation.Workout.TrackScreen.ROUTE_ID_ARG) {
                                            type = NavType.StringType
                                        },

                                        )
                                ) { backStackEntry ->
                                    val routeId = backStackEntry.arguments?.getString(
                                        AppNavigation.Workout.TrackScreen.ROUTE_ID_ARG
                                    )
                                    TrackCompleteScreen(routeId, trackingService) {
                                        navController.navigate(
                                            AppNavigation.Home.HOME_FEATURE_SCREEN_ROUTE,
                                            navOptions = navOptions {
                                                popUpTo(AppNavigation.Home.HOME_FEATURE_SCREEN_ROUTE) {
                                                    inclusive = true
                                                }
                                            })
                                    }
                                }
                            }
                            composable(
                                AppNavigation.Home.HOME_FEATURE_SCREEN_ROUTE,
                            ) {
                                HomeCompleteScreen(
                                    navigateToTrackScreen = {
                                        navController.navigate(
                                            AppNavigation.Workout.TrackScreen.createTrackScreen(
                                                it
                                            ), navOptions = navOptions {
                                                popUpTo(AppNavigation.Home.HOME_FEATURE_SCREEN_ROUTE) {
                                                    inclusive = true
                                                }
                                            })
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                launcher.launch(permission)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isTrackingServiceBound = false
    }
}