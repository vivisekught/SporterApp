package com.graduate.work.sporterapp.features.home

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.core.snackbar.ProvideSnackbarController
import com.graduate.work.sporterapp.core.snackbar.SnackbarMessage
import com.graduate.work.sporterapp.core.snackbar.SnackbarMessageHandler
import com.graduate.work.sporterapp.core.snackbar.UserMessage
import com.graduate.work.sporterapp.domain.api.usecases.GetAndSaveStravaTokenUseCase
import com.graduate.work.sporterapp.features.home.screens.profile.ProfileScreen
import com.graduate.work.sporterapp.features.home.screens.route_builder.RouteBuilderCompleteScreen
import com.graduate.work.sporterapp.features.home.screens.route_builder.screen.RouteBuilderScreenEvent
import com.graduate.work.sporterapp.features.home.screens.saved_route_page.screen.RoutePageScreenCompleteScreen
import com.graduate.work.sporterapp.features.home.screens.saved_routes.screen.SavedRoutesScreen
import com.graduate.work.sporterapp.features.home.screens.workout_page.screen.WorkoutPageCompleteScreen
import com.graduate.work.sporterapp.features.home.screens.workouts.screen.WorkoutsScreen
import com.graduate.work.sporterapp.navigation.AppNavigation
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BottomNavItem(
    @StringRes val screenNameId: Int,
    val selectedIcon: ImageVector,
    val route: String,
)

@Composable
fun HomeCompleteScreen(navigateToTrackScreen: (routeId: String?) -> Unit, onSignOut: () -> Unit) {
    val vm = hiltViewModel<HomeCompleteViewModel>()
    val bottomNavController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val listOfScreens = listOf(
        BottomNavItem(
            screenNameId = R.string.workouts,
            selectedIcon = Icons.AutoMirrored.Filled.DirectionsBike,
            route = AppNavigation.Home.WorkoutsScreen.route
        ),
        BottomNavItem(
            screenNameId = R.string.saved_routes,
            selectedIcon = Icons.Filled.Route,
            route = AppNavigation.Home.SavedRoutesScreen.route
        ),
        BottomNavItem(
            screenNameId = R.string.create_route,
            selectedIcon = Icons.Filled.Create,
            route = AppNavigation.Home.CreateRouteScreen.route
        ),
        BottomNavItem(
            screenNameId = R.string.profile,
            selectedIcon = Icons.Filled.Person,
            route = AppNavigation.Home.ProfileScreen.route
        ),
    )
    ProvideSnackbarController(
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope
    ) {
        var snackbarMessage: SnackbarMessage? by remember {
            mutableStateOf(null)
        }
        SnackbarMessageHandler(
            snackbarMessage = snackbarMessage,
            onDismissSnackbar = {  },
        )
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize()
        ) {
            NavHost(
                navController = bottomNavController,
                startDestination = AppNavigation.Home.WorkoutsScreen.route,
                modifier = Modifier.weight(1f)
            ) {
                composable(
                    AppNavigation.Home.RedirectStravaScreen.route, deepLinks = listOf(
                        navDeepLink {
                            uriPattern = "strava://redirect?code={code}"
                        })
                ) { navBackStackEntry ->
                    val code = navBackStackEntry.arguments?.getString("code")
                    if (!code.isNullOrEmpty()) {
                        vm.getAndSaveStravaToken(code = code, onError = {
                            snackbarMessage = SnackbarMessage.from(UserMessage.from("Strava auth error"))
                        }, onSuccess = {
                            snackbarMessage = SnackbarMessage.from(UserMessage.from("Strava auth success"))
                        })
                        bottomNavController.navigate(AppNavigation.Home.WorkoutsScreen.route, navOptions = navOptions {
                            popUpTo(AppNavigation.Home.WorkoutsScreen.route) { inclusive = true }
                        })
                    }
                }
                composable(AppNavigation.Home.CreateRouteScreen.route) {
                    RouteBuilderCompleteScreen(snackbarHostState)
                }
                composable(AppNavigation.Home.WorkoutsScreen.route) {
                    WorkoutsScreen(snackbarHostState = snackbarHostState, navToWorkoutPage = {
                        bottomNavController.navigate(
                            AppNavigation.Home.WorkoutPageScreen.createWorkoutPageScreen(it)
                        )
                    }, navToWorkout = {
                        navigateToTrackScreen("routeId")
                    })
                }
                composable(AppNavigation.Home.WorkoutPageScreen.route, arguments = listOf(
                    navArgument(
                        name = AppNavigation.Home.WorkoutPageScreen.WORKOUT_ID_ARG,
                    ) {
                        type = NavType.StringType
                    }
                )) {
                    val workoutId =
                        it.arguments?.getString(AppNavigation.Home.WorkoutPageScreen.WORKOUT_ID_ARG)
                    workoutId?.let {
                        WorkoutPageCompleteScreen(
                            snackbarHostState = snackbarHostState,
                            workoutId = workoutId,
                        ) {
                            bottomNavController.popBackStack()
                        }
                    }
                }
                composable(
                    AppNavigation.Home.SavedRoutesScreen.route
                ) {
                    SavedRoutesScreen(snackbarHostState, navToRoutePage =  { routeId ->
                        bottomNavController.navigate(
                            AppNavigation.Home.RoutePageScreen.createRoutePageScreen(
                                routeId
                            )
                        )
                    }, navToWorkout = {
                        navigateToTrackScreen("routeId")
                    })
                }
                composable(
                    AppNavigation.Home.ProfileScreen.route
                ) {
                    ProfileScreen(snackbarHostState) {
                        onSignOut()
                    }
                }
                composable(AppNavigation.Home.RoutePageScreen.route, arguments = listOf(
                    navArgument(
                        name = AppNavigation.Home.RoutePageScreen.ROUTE_ID_ARG,
                    ) {
                        type = NavType.StringType
                    }
                )) { backStackEntry ->
                    val routeId =
                        backStackEntry.arguments?.getString(AppNavigation.Home.RoutePageScreen.ROUTE_ID_ARG)
                    routeId?.let {
                        RoutePageScreenCompleteScreen(
                            snackbarHostState = snackbarHostState,
                            routeId = routeId,
                            startWorkout = {
                                navigateToTrackScreen(it)
                            }, onBack = {
                                bottomNavController.popBackStack()
                            })
                    }
                }
            }
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                listOfScreens.forEach { screen ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            bottomNavController.navigate(screen.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = screen.selectedIcon,
                                contentDescription = stringResource(id = screen.screenNameId)
                            )
                        },
                        label = {
                            Text(text = stringResource(id = screen.screenNameId))
                        })
                }
            }
        }
    }
}

@HiltViewModel
class HomeCompleteViewModel @Inject constructor(
    private val getAndSaveStravaTokenUseCase: GetAndSaveStravaTokenUseCase
) : ViewModel() {

    fun getAndSaveStravaToken(code: String, onError: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val token = getAndSaveStravaTokenUseCase(code = code)
            Log.d("AAAAAA", "getAndSaveStravaToken: $token")
            if (token != null) {
                onSuccess()
            } else {
                onError()
            }
        }
    }
}
