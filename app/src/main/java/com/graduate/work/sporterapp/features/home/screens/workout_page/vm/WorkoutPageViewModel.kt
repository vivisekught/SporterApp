package com.graduate.work.sporterapp.features.home.screens.workout_page.vm

import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduate.work.sporterapp.R
import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.core.graph.GraphProfile
import com.graduate.work.sporterapp.core.snackbar.SnackbarMessage
import com.graduate.work.sporterapp.core.snackbar.UserMessage
import com.graduate.work.sporterapp.domain.api.usecases.UploadGpxWorkoutToStravaUseCase
import com.graduate.work.sporterapp.domain.firebase.storage.workouts.entity.Workout
import com.graduate.work.sporterapp.domain.firebase.storage.workouts.usecases.GetWorkoutByIdUseCase
import com.graduate.work.sporterapp.domain.maps.files.usecases.GetGpxFileIntentUseCase
import com.graduate.work.sporterapp.domain.maps.files.usecases.GetTcxFileIntentUseCase
import com.mapbox.geojson.Point
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

data class WorkoutPageState(
    val workout: Workout? = null,
    val isLoading: Boolean = false,
    val snackbarMessage: SnackbarMessage? = null,
    val workoutFileIntent: Intent? = null,
    val graphElevationProfile: GraphProfile? = null,
    val graphSpeedProfile: GraphProfile? = null,
    val mapPoint: Point? = null,
)

@HiltViewModel(assistedFactory = WorkoutPageViewModel.WorkoutPageViewModelFactory::class)
class WorkoutPageViewModel @AssistedInject constructor(
    @Assisted val id: String,
    getWorkoutByIdUseCase: GetWorkoutByIdUseCase,
    private val getGpxFileIntentUseCase: GetGpxFileIntentUseCase,
    private val getTcxFileIntentUseCase: GetTcxFileIntentUseCase,
    private val uploadGpxWorkoutToStravaUseCase: UploadGpxWorkoutToStravaUseCase,
) : ViewModel() {

    var state by mutableStateOf(WorkoutPageState(isLoading = true))
        private set

    init {
        getWorkoutByIdUseCase.invoke(
            id = id,
            onError = { onGetWorkoutError() },
            onSuccess = {
                onGetWorkoutSuccess(it)
            })
    }

    fun deleteRoute() {

    }

    fun exportWorkoutAsGpx() {
        state.workout?.let { workout ->
            viewModelScope.launch {
                val intent = getGpxFileIntentUseCase(workout)
                state = if (intent != null) {
                    state.copy(workoutFileIntent = intent)
                } else {
                    state.copy(
                        snackbarMessage = SnackbarMessage.from(UserMessage.from(R.string.gpx_export_error))
                    )
                }
            }
        }
    }

    fun exportWorkoutAsTcx() {
        state.workout?.let { workout ->
            viewModelScope.launch {
                val intent = getTcxFileIntentUseCase(workout)
                state = if (intent != null) {
                    state.copy(workoutFileIntent = intent)
                } else {
                    state.copy(
                        snackbarMessage = SnackbarMessage.from(UserMessage.from("Tcx export error"))
                    )
                }
            }
        }
    }

    private fun onGetWorkoutError() {
        state = state.copy(
            isLoading = false,
            snackbarMessage = SnackbarMessage.from(UserMessage.from("Workout loading error"))
        )
    }

    private fun onGetWorkoutSuccess(workout: Workout) {
        val elevationsProfileX = workout.points?.map { it.distanceFromStart }
        val elevationsProfileY = workout.points?.map { it.point.altitude() }
        val speedProfileX = workout.points?.map { it.distanceFromStart }
        val speedProfileY = workout.points?.map { it.speed }

        state = state.copy(
            isLoading = false,
            workout = workout,
            graphElevationProfile = GraphProfile(elevationsProfileX, elevationsProfileY),
            graphSpeedProfile = GraphProfile(speedProfileX, speedProfileY),
        )
    }

//    fun showMapPoint(distance: Double) {
//        val closestValue = state.graphElevationProfile?.x?.closestValue(distance)
//        state.graphElevationProfile?.x?.indexOf(closestValue)?.let { index ->
//            if (index > 0) {
//                state = state.copy(mapPoint = state.route?.points?.get(index))
//            }
//        }
//    }

    fun hideMapPoint() {
        state = state.copy(mapPoint = null)
    }

    fun dismissSnackbar() {
        state = state.copy(snackbarMessage = null)
    }

    fun exportWorkoutToStrava() {
        state.workout?.let { workout ->
            viewModelScope.launch {
                when (val response = uploadGpxWorkoutToStravaUseCase(workout)) {
                    is Response.Failure -> {
                        state = state.copy(
                            snackbarMessage = SnackbarMessage.from(UserMessage.from(response.message)),
                            isLoading = false
                        )
                    }

                    Response.Loading -> {
                        state = state.copy(isLoading = true)
                    }

                    is Response.Success -> {
                        state = state.copy(
                            isLoading = false,
                            snackbarMessage = SnackbarMessage.from(UserMessage.from("Workout uploaded to Strava"))
                        )
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface WorkoutPageViewModelFactory {
        fun create(id: String): WorkoutPageViewModel
    }
}