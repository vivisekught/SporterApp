package com.graduate.work.sporterapp.features.home.screens.map.route_builder.vm

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.core.ext.getAlphabetLetterByIndex
import com.graduate.work.sporterapp.core.ext.move
import com.graduate.work.sporterapp.core.ext.toPoint
import com.graduate.work.sporterapp.core.map.LocationServiceResult
import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.core.snackbar.SnackbarMessage
import com.graduate.work.sporterapp.core.snackbar.UserMessage
import com.graduate.work.sporterapp.domain.firebase.storage.routes.entity.Route
import com.graduate.work.sporterapp.domain.firebase.storage.routes.usecases.SaveRouteInFirestoreUseCase
import com.graduate.work.sporterapp.domain.maps.location.usecases.GetUserLocationUseCase
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.MapPoint
import com.graduate.work.sporterapp.domain.maps.mapbox.usecases.GetRouteFromCoordinatesUseCase
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class RouteBuilderState(
    val currentMapStyle: MapBoxStyle = MapBoxStyle.STREET,
    val userLocationPoint: Point? = null,
    val lastSelectedPoint: Point? = null,
    val pointAlphabetIndex: Int = 0,
    val userPoints: List<MapPoint> = emptyList(),
    val route: Route? = null,
    val isRouteLoading: Boolean = false,
    val isNewPointDialogOpened: Boolean = false,
    val snackbarMessage: SnackbarMessage? = null,
)

@HiltViewModel
class RouteBuilderScreenViewModel @Inject constructor(
    private val getUserLocationUseCase: GetUserLocationUseCase,
    private val getRouteFromCoordinatesUseCase: GetRouteFromCoordinatesUseCase,
    private val saveRouteInFirestoreUseCase: SaveRouteInFirestoreUseCase,
) : ViewModel() {

    var state by mutableStateOf(RouteBuilderState())
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
                state.pointAlphabetIndex.getAlphabetLetterByIndex(),
                point
            ),
            pointAlphabetIndex = state.pointAlphabetIndex + 1
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
            delay(NEW_POINT_DIALOG_ANIMATION_DURATION)
            state = state.copy(lastSelectedPoint = null)
        }
    }

    private fun clearRoute(isEmpty: Boolean = true) {
        state = state.copy(
            route = null,
            userPoints = emptyList(),
            pointAlphabetIndex = if (isEmpty) 0 else state.pointAlphabetIndex
        )
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
                        state.copy(snackbarMessage = SnackbarMessage.from(UserMessage.from(response.message)))
                    }

                    is Response.Success -> {
                        state.copy(route = response.data)
                    }
                }
            }
            Log.d("AAAAAA", "getRoute vm: ${state.route?.points}")
        }
    }

    fun changeMapStyle(style: MapBoxStyle) {
        state = state.copy(currentMapStyle = style)
    }

    fun deletePoint(index: Int) {
        state = state.copy(userPoints = state.userPoints - state.userPoints[index])
        if (state.userPoints.size < 2) {
            clearRoute(state.userPoints.isEmpty())
        } else {
            getRoute()
        }
    }

    fun changePointByIndex(from: Int, to: Int) {
        state = state.copy(
            userPoints = state.userPoints.toMutableList().move(from, to)
        )
        getRoute()
    }

    fun setFirstSelectedPointAsDestination() {
        state.userPoints.getOrNull(0)?.let { addPoint(it.point) }
    }

    fun saveRoute(routeName: String, routeDescription: String) {
        viewModelScope.launch {
            state = state.copy(isRouteLoading = true)
            saveRouteAttributes(routeName, routeDescription)
            state.route?.let {
                saveRouteInFirestoreUseCase(it, state.currentMapStyle) { error ->
                    state = state.copy(isRouteLoading = false)
                    if (error == null) {
                        state = state.copy(
                            snackbarMessage = SnackbarMessage.from(
                                UserMessage.from(R.string.route_saved)
                            )
                        )
                        clearRoute()
                    } else {
                        state = state.copy(
                            snackbarMessage = SnackbarMessage.from(
                                UserMessage.from(R.string.route_saving_error),
                            )
                        )
                    }

                }
            }
        }
    }

    private fun saveRouteAttributes(routeName: String, routeDescription: String) {
        state =
            state.copy(
                route = state.route?.copy(
                    name = routeName,
                    description = routeDescription,
                    timeStamp = Date().time
                ),
            )
    }

    fun dismissSnackbar() {
        state = state.copy(snackbarMessage = null)
    }

    companion object {
        private const val NEW_POINT_DIALOG_ANIMATION_DURATION = 300L
    }
}