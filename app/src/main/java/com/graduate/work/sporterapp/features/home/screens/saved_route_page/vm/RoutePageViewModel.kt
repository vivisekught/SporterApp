package com.graduate.work.sporterapp.features.home.screens.saved_route_page.vm

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.core.ext.closestValue
import com.graduate.work.sporterapp.core.snackbar.SnackbarMessage
import com.graduate.work.sporterapp.core.snackbar.UserMessage
import com.graduate.work.sporterapp.domain.firebase.storage.routes.usecases.GetRouteByIdUseCase
import com.graduate.work.sporterapp.domain.maps.gpx.usecases.GetGpxFileIntentUseCase
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import com.mapbox.geojson.Point
import com.mapbox.turf.TurfMeasurement
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch


data class ElevationProfile(
    val x: List<Double>?,
    val y: List<Double>?,
)
data class RoutePageState(
    val route: Route? = null,
    val isLoading: Boolean = false,
    val snackbarMessage: SnackbarMessage? = null,
    val gpxFileIntent: Intent? = null,
    val elevationProfile: ElevationProfile? = null,
    val mapPoint: Point? = null,
)

@HiltViewModel(assistedFactory = RoutePageViewModel.RoutePageViewModelFactory::class)
class RoutePageViewModel @AssistedInject constructor(
    @Assisted val id: String,
    getRouteByIdUseCase: GetRouteByIdUseCase,
    private val getGpxFileIntentUseCase: GetGpxFileIntentUseCase,
) : ViewModel() {

    var state by mutableStateOf(RoutePageState())
        private set

    init {
        getRouteByIdUseCase.invoke(id, ::onGetRouteError, ::onGetRouteSuccess)
    }

    fun deleteRoute() {

    }

    fun exportRoute() {
        state.route?.let { route ->
            viewModelScope.launch {
                val intent = getGpxFileIntentUseCase(route)
                state = if (intent != null) {
                    state.copy(gpxFileIntent = intent)
                } else {
                    state.copy(
                        snackbarMessage = SnackbarMessage.from(UserMessage.from(R.string.gpx_export_error))
                    )
                }
            }
        }

    }

    private fun onGetRouteError(error: Throwable) {
        state = state.copy(
            isLoading = false,
            snackbarMessage = SnackbarMessage.from(UserMessage.from(R.string.route_loading_error))
        )
    }

    private fun onGetRouteSuccess(route: Route) {
        Log.d("AAAAAA", "route: ${route.points}")
        val elevationsProfileY = route.points?.map { it.altitude() }
        var distance = 0.0
        val elevationsProfileX = route.points?.mapIndexed { index, point ->
            if (index > 0) {
                distance += TurfMeasurement.distance(route.points[index - 1], point)
            }
            distance
        }
        Log.d("AAAAAA", "elevationsProfileX: ${elevationsProfileX}")
        state = state.copy(
            isLoading = false,
            route = route,
            elevationProfile = ElevationProfile(elevationsProfileX, elevationsProfileY),
        )
    }

    fun showMapPoint(distance: Double) {
        Log.d("AAAAAA", "showMapPoint, distance: $distance")
        val closestValue = state.elevationProfile?.x?.closestValue(distance)
        state.elevationProfile?.x?.indexOf(closestValue)?.let { index ->
            if (index > 0) {
                state = state.copy(mapPoint = state.route?.points?.get(index))
            }
        }
    }

    fun hideMapPoint() {
        state = state.copy(mapPoint = null)
    }

    fun dismissSnackbar() {
        state = state.copy(snackbarMessage = null)
    }

    @AssistedFactory
    interface RoutePageViewModelFactory {
        fun create(id: String): RoutePageViewModel
    }
}