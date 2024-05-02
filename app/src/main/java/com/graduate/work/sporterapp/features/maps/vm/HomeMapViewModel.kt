package com.graduate.work.sporterapp.features.maps.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.core.ext.toPoint
import com.graduate.work.sporterapp.core.map.LocationServiceResult
import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.domain.maps.location.usecases.GetUserLocationUseCase
import com.graduate.work.sporterapp.domain.maps.mapbox.usecases.GetRouteFromCoordinatesUseCase
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeMapState(
    val currentMapStyle: MapBoxStyle = MapBoxStyle.STREET,
    val userLocationPoint: Point? = null,
    val lastSelectedPoint: Point? = null,
    val userPoints: List<Point> = emptyList(),
    val route: List<Point>? = null,
    val routeNotFoundError: Boolean = false,
    val isRouteLoading: Boolean = false,
)

@HiltViewModel
class HomeMapViewModel @Inject constructor(
    private val getUserLocationUseCase: GetUserLocationUseCase,
    private val getRouteFromCoordinatesUseCase: GetRouteFromCoordinatesUseCase,
) : ViewModel() {

    var state by mutableStateOf(HomeMapState())
        private set

    init {
        collectUserLocation()
    }

    private fun collectUserLocation() {
        viewModelScope.launch {
            getUserLocationUseCase().collect { userLocation ->
                if (userLocation is LocationServiceResult.Success) {
                    state = state.copy(
                        userLocationPoint = userLocation.location.toPoint()
                    )
                }
            }
        }
    }

    fun changeLastSelectedPoint(point: Point) {
        state = state.copy(lastSelectedPoint = point)
    }

    fun setLastSelectedPointAsDestination() {
        state.lastSelectedPoint?.let { addPoint(it) }
    }

    fun setLastSelectedPointAsStart() {
        clearRoute()
        clearUserPoints()
        state.lastSelectedPoint?.let { addPoint(it) }
    }

    fun setUserLocationAsStart() {
        state.lastSelectedPoint?.let {
            addPoint(state.userLocationPoint ?: it)
            addPoint(it)
        }
    }

    private fun addPoint(point: Point) {
        state = state.copy(userPoints = state.userPoints + point)
        deleteLastSelectedPoint()
        getRoute()
    }

    private fun clearUserPoints() {
        state = state.copy(userPoints = emptyList())
    }

    fun deleteLastSelectedPoint() {
        state = state.copy(lastSelectedPoint = null)
    }

    private fun clearRoute() {
        state = state.copy(route = null)
    }

    private fun getRoute() {
        if (state.userPoints.size < 2) {
            return
        }
        state = state.copy(isRouteLoading = true)
        viewModelScope.launch {
            getRouteFromCoordinatesUseCase(state.userPoints).let { response ->
                state = state.copy(isRouteLoading = false)
                state = when (response) {
                    is Response.Failure -> {
                        state.copy(routeNotFoundError = true)
                    }

                    is Response.Success -> {
                        state.copy(route = response.data)
                    }
                }
            }
        }
    }

    fun resetRouteError() {
        state = state.copy(routeNotFoundError = false)
    }

    fun changeMapStyle(style: MapBoxStyle) {
        state = state.copy(currentMapStyle = style)
    }
}