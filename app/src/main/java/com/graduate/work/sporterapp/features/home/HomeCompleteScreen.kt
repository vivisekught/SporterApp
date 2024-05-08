package com.graduate.work.sporterapp.features.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.features.home.screens.map.route_builder.screen.RouteBuilderCompleteScreen
import com.graduate.work.sporterapp.features.home.screens.profile.ProfileScreen
import com.graduate.work.sporterapp.features.home.screens.saved_routes.screens.SavedRouteScreen
import com.graduate.work.sporterapp.navigation.AppNavigation

data class BottomNavItem(
    @StringRes val screenNameId: Int,
    val selectedIcon: ImageVector,
    val route: String,
)

@Immutable
data class ScaffoldScreensState(
    val bottomNavigation: @Composable (() -> Unit)? = null,
    @StringRes val screenNameId: Int? = null,
    val fabIcon: @Composable (() -> Unit)? = null,
    val actions: @Composable (() -> Unit)? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCompleteScreen() {
    val bottomNavController = rememberNavController()
    val listOfScreens = listOf(
        BottomNavItem(
            screenNameId = R.string.saved_routes,
            selectedIcon = Icons.Filled.Bookmark,
            route = AppNavigation.Home.SavedRouteScreen.route
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
    val scaffoldScreensState = remember {
        mutableStateOf(ScaffoldScreensState())
    }
    Scaffold(
        bottomBar = {
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
        },
        topBar = {
            TopAppBar(
                title = {
                    scaffoldScreensState.value.screenNameId?.let {
                        Text(text = stringResource(id = it))
                    }
                },
                actions = {
                    scaffoldScreensState.value.actions?.invoke()
                },
            )
        },
        floatingActionButton = {
            scaffoldScreensState.value.fabIcon?.invoke()
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = AppNavigation.Home.HomeMapScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppNavigation.Home.HomeMapScreen.route) {
                RouteBuilderCompleteScreen { scaffoldScreensState.value = it }
            }
            composable(AppNavigation.Home.SavedRouteScreen.route) {
                LaunchedEffect(Unit) {
                    scaffoldScreensState.value = ScaffoldScreensState(
                        screenNameId = R.string.saved_routes,
                        fabIcon = null,
                        actions = {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                        }
                    )
                }
                SavedRouteScreen()
            }
            composable(AppNavigation.Home.ProfileScreen.route) {
                LaunchedEffect(Unit) {
                    scaffoldScreensState.value = ScaffoldScreensState(
                        screenNameId = R.string.profile,
                        fabIcon = null,
                        actions = null
                    )
                }
                ProfileScreen()
            }
        }
    }
}