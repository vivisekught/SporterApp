package com.graduate.work.sporterapp.features.home.screens.map.route_builder

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.graduate.work.sporterapp.core.snackbar.LocalSnackbarController
import com.graduate.work.sporterapp.core.snackbar.SnackbarController
import com.graduate.work.sporterapp.features.home.screens.map.route_builder.screen.RouteBuilderScreen
import com.graduate.work.sporterapp.features.home.screens.map.route_builder.screen.RouteBuilderScreenEvent
import com.graduate.work.sporterapp.features.home.screens.map.route_builder.vm.RouteBuilderScreenViewModel

@Composable
fun RouteBuilderCompleteScreen(
    snackbarHostState: SnackbarHostState,
    snackbarController: SnackbarController = LocalSnackbarController.current,
) {
    val viewModel: RouteBuilderScreenViewModel = hiltViewModel<RouteBuilderScreenViewModel>()
    RouteBuilderScreen(snackbarHostState, viewModel.state) { event ->
        when (event) {
            RouteBuilderScreenEvent.SetLastPointAsDestination -> {
                viewModel.setLastSelectedPointAsDestination()
            }

            RouteBuilderScreenEvent.SetLastSelectedPointAsStart -> {
                viewModel.setLastSelectedPointAsStart()
            }

            is RouteBuilderScreenEvent.ChangeLastSelectedPoint -> {
                viewModel.changeLastSelectedPoint(event.point)
            }

            RouteBuilderScreenEvent.DeleteLastSelectedPoint -> {
                viewModel.deleteLastSelectedPoint()
            }

            RouteBuilderScreenEvent.SetUserLocationAsStart -> {
                viewModel.setUserLocationAsStart()
            }

            is RouteBuilderScreenEvent.ChangeRouteBuilderStyle -> {
                viewModel.changeMapStyle(event.style)
            }

            is RouteBuilderScreenEvent.OnPointDeleteClick -> {
                viewModel.deletePoint(event.index)
            }

            is RouteBuilderScreenEvent.OnPointIndexChanged -> {
                viewModel.changePointByIndex(event.from, event.to)
            }

            RouteBuilderScreenEvent.SetFirstPointAsDestination -> {
                viewModel.setFirstSelectedPointAsDestination()
            }

            is RouteBuilderScreenEvent.SaveRoute -> {
                viewModel.saveRoute(event.routeName, event.routeDescription)
            }

            RouteBuilderScreenEvent.DismissSnackbar -> {
                viewModel.dismissSnackbar()
            }

            is RouteBuilderScreenEvent.ShowSnackbar -> {
                snackbarController.showMessage(event.message)
            }
        }
    }
}