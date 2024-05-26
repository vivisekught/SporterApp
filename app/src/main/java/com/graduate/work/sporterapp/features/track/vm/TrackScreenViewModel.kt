package com.graduate.work.sporterapp.features.track.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.core.snackbar.SnackbarMessage
import com.graduate.work.sporterapp.core.snackbar.UserMessage
import com.graduate.work.sporterapp.domain.firebase.storage.routes.usecases.GetRouteByIdUseCase
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch


data class TrackScreenUiState(
    val preparedRoute: Route? = null,
    val currentMapStyle: MapBoxStyle = MapBoxStyle.STREET,
    val snackbarMessage: SnackbarMessage? = null,
)

@HiltViewModel(assistedFactory = TrackScreenViewModel.TrackScreenViewModelFactory::class)
class TrackScreenViewModel @AssistedInject constructor(
    @Assisted val routeId: String?,
    private val getRouteByIdUseCase: GetRouteByIdUseCase,
) : ViewModel() {

    var state by mutableStateOf(TrackScreenUiState())
        private set

    fun getRoute(onRouteLoaded: (route: Route?) -> Unit) {
        viewModelScope.launch {
            routeId?.let {
                getRouteByIdUseCase(it,
                    onError = {
                        onGetRouteError()
                        onRouteLoaded(null)
                    },
                    onSuccess = { route: Route ->
                        onGetRouteSuccess(route)
                        onRouteLoaded(route)
                    })
            }
        }
    }

    private fun onGetRouteError() {
        state = state.copy(
            snackbarMessage = SnackbarMessage.from(UserMessage.from(R.string.route_loading_error))
        )
    }

    private fun onGetRouteSuccess(route: Route) {
        state = state.copy(preparedRoute = route)
    }

    fun changeMapStyle(style: MapBoxStyle) {
        state = state.copy(currentMapStyle = style)
    }

    fun workoutSavedError(message: String) {
        state = state.copy(snackbarMessage = SnackbarMessage.from(UserMessage.from(message)))
    }

    @AssistedFactory
    interface TrackScreenViewModelFactory {
        fun create(id: String?): TrackScreenViewModel
    }
}