package com.graduate.work.sporterapp.features.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.core.snackbar.ProvideSnackbarController
import com.graduate.work.sporterapp.features.home.screens.map.route_builder.RouteBuilderCompleteScreen
import com.graduate.work.sporterapp.features.home.screens.profile.ProfileScreen
import com.graduate.work.sporterapp.features.home.screens.saved_route_page.screen.RoutePageScreenCompleteScreen
import com.graduate.work.sporterapp.features.home.screens.saved_routes.screens.SavedRoutesScreen
import com.graduate.work.sporterapp.navigation.AppNavigation

data class BottomNavItem(
    @StringRes val screenNameId: Int,
    val selectedIcon: ImageVector,
    val route: String,
)

@Composable
fun HomeCompleteScreen() {
    val bottomNavController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val listOfScreens = listOf(
        BottomNavItem(
            screenNameId = R.string.saved_routes,
            selectedIcon = Icons.Filled.Bookmark,
            route = AppNavigation.Home.SavedRoutesScreen.route
        ),
        BottomNavItem(
            screenNameId = R.string.home,
            selectedIcon = Icons.Filled.Home,
            route = AppNavigation.Home.HomeMapScreen.route
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
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize()
        ) {
            NavHost(
                navController = bottomNavController,
                startDestination = AppNavigation.Home.SavedRoutesScreen.route,
                modifier = Modifier.weight(1f)
            ) {
                composable(AppNavigation.Home.HomeMapScreen.route) {
                    RouteBuilderCompleteScreen(snackbarHostState)
                }
                composable(AppNavigation.Home.SavedRoutesScreen.route) {
                    SavedRoutesScreen(snackbarHostState) { routeId ->
                        bottomNavController.navigate(
                            AppNavigation.Home.RoutePageScreen.createRoutePageScreen(
                                routeId
                            )
                        )
                    }
                }
                composable(AppNavigation.Home.ProfileScreen.route) {
                    ProfileScreen()
                }
                composable(AppNavigation.Home.RoutePageScreen.route, arguments = listOf(
                    navArgument(
                        name = AppNavigation.Home.RoutePageScreen.routeIdArg,
                    ) {
                        type = NavType.StringType
                    }
                )) { backStackEntry ->
                    val routeId =
                        backStackEntry.arguments?.getString(AppNavigation.Home.RoutePageScreen.routeIdArg)
                    routeId?.let {
                        RoutePageScreenCompleteScreen(snackbarHostState, routeId) {
                            bottomNavController.popBackStack()
                        }
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