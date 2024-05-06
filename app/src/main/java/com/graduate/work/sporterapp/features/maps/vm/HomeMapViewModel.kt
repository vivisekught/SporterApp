package com.graduate.work.sporterapp.features.maps.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.core.ext.getAlphabetLetterByIndex
import com.graduate.work.sporterapp.core.ext.move
import com.graduate.work.sporterapp.core.ext.toPoint
import com.graduate.work.sporterapp.core.map.LocationServiceResult
import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.domain.maps.location.usecases.GetUserLocationUseCase
import com.graduate.work.sporterapp.domain.maps.mapbox.domain.MapPoint
import com.graduate.work.sporterapp.domain.maps.mapbox.usecases.GetRouteFromCoordinatesUseCase
import com.graduate.work.sporterapp.features.maps.screen.HomeScreenConstants.NEW_POINT_DIALOG_ANIMATION_DURATION
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeMapState(
    val currentMapStyle: MapBoxStyle = MapBoxStyle.STREET,
    val userLocationPoint: Point? = null,
    val lastSelectedPoint: Point? = null,
    val numOfAllUserPoints: Int = 0,
    val userPoints: List<MapPoint> = emptyList(),
    val route: List<Point>? = null,
    val routeNotFoundError: Boolean = false,
    val isRouteLoading: Boolean = false,
    val isNewPointDialogOpened: Boolean = false,
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
        state = state.copy(lastSelectedPoint = point, isNewPointDialogOpened = true)
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
        state = state.copy(
            userPoints = state.userPoints + MapPoint(
                state.numOfAllUserPoints.getAlphabetLetterByIndex(),
                point
            ),
            numOfAllUserPoints = state.numOfAllUserPoints + 1
        )
        deleteLastSelectedPoint()
        getRoute()
    }

    private fun clearUserPoints() {
        state = state.copy(userPoints = emptyList())
    }

    fun deleteLastSelectedPoint() {
        viewModelScope.launch {
            state = state.copy(isNewPointDialogOpened = false)
            // delay for animation
            delay(NEW_POINT_DIALOG_ANIMATION_DURATION.toLong())
            state = state.copy(lastSelectedPoint = null)
        }
    }

    private fun clearRoute() {
        state = state.copy(route = null, routeNotFoundError = false, numOfAllUserPoints = 0)
    }

    private fun getRoute() {
        if (state.userPoints.size < 2) {
            return
        }
        state = state.copy(isRouteLoading = true)
        viewModelScope.launch {
            val points = state.userPoints.map { it.point }
            getRouteFromCoordinatesUseCase(points).let { response ->
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

    fun deletePoint(index: Int) {
        state = state.copy(userPoints = state.userPoints - state.userPoints[index])
        getRoute()
    }

    fun changePointByIndex(from: Int, to: Int) {
        state = state.copy(
            userPoints = state.userPoints.toMutableList().move(from, to)
        )
        getRoute()
    }
}