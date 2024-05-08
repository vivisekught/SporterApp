package com.graduate.work.sporterapp.features.home.screens.map.home_map.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.core.ext.toPoint
import com.graduate.work.sporterapp.core.map.LocationServiceResult
import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.domain.maps.location.usecases.GetUserLocationUseCase
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeMapState(
    val currentMapStyle: MapBoxStyle = MapBoxStyle.STREET,
    val userLocationPoint: Point? = null,
    val lastSelectedPoint: Point? = null,
    val isNewPointDialogOpened: Boolean = false,
    val userPoints: List<Point> = emptyList(),
    val listOfUserMarkersState: Boolean = false,
)

@HiltViewModel
class HomeMapViewModel @Inject constructor(
    private val getUserLocationUseCase: GetUserLocationUseCase,
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

    fun changeMapStyle(style: MapBoxStyle) {
        state = state.copy(currentMapStyle = style)
    }

    fun changeLastSelectedPoint(point: Point) {
        state = state.copy(lastSelectedPoint = point, isNewPointDialogOpened = true)
    }

    fun deleteLastSelectedPoint() {
        state = state.copy(isNewPointDialogOpened = false, lastSelectedPoint = null)
    }

    fun toggleListOfUserMarkers() {
        state = state.copy(listOfUserMarkersState = !state.listOfUserMarkersState)
    }
}