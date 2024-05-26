package com.graduate.work.sporterapp.features.track

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.graduate.work.sporterapp.data.maps.location.TrackingUserWorkoutService
import com.graduate.work.sporterapp.features.track.screens.TrackScreen
import com.graduate.work.sporterapp.features.track.screens.TrackScreenEvent
import com.graduate.work.sporterapp.features.track.vm.TrackScreenViewModel

@Composable
fun TrackCompleteScreen(
    routeId: String?,
    trackingService: TrackingUserWorkoutService,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val vm =
        hiltViewModel<TrackScreenViewModel, TrackScreenViewModel.TrackScreenViewModelFactory> { factory ->
            factory.create(routeId)
        }
    LaunchedEffect(Unit) {
        with(context) {
            vm.getRoute { route ->
                if (route == null || route.routeId.isEmpty()) return@getRoute
                val intent = TrackingUserWorkoutService.createRouteIntent(this, route)
                startService(intent)
            }
        }
    }

    TrackScreen(trackingService, uiState = vm.state) {
        when (it) {
            is TrackScreenEvent.ChangeMapStyle -> {
                vm.changeMapStyle(it.style)
            }

            TrackScreenEvent.OnBack -> {
                onBack()
            }

            is TrackScreenEvent.StopWorkout -> {
                if (trackingService.points.isEmpty() || trackingService.points.size < 2) {
                    vm.workoutSavedError("Very short workout")
                    return@TrackScreen
                }
                trackingService.saveWorkout(it.name) {
                    onBack()
                }
            }
        }
    }
}