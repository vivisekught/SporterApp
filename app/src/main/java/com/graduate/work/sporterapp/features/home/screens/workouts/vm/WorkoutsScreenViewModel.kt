package com.graduate.work.sporterapp.features.home.screens.workouts.vm

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.GetUserIdUseCase
import com.graduate.work.sporterapp.domain.firebase.storage.workout.CloudStorageWorkoutRepository
import com.graduate.work.sporterapp.domain.firebase.storage.workout.entity.Workout
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WorkoutsScreenViewModel @Inject constructor(
    private val getUserIdUseCase: GetUserIdUseCase,
    private val cloudStorageWorkoutRepository: CloudStorageWorkoutRepository,
) : ViewModel() {

    var workouts = mutableStateMapOf<String, Workout>()
        private set

    fun addListener() {
        val userId = getUserIdUseCase()
        cloudStorageWorkoutRepository.addListener(
            userId.toString(),
            onDocumentEvent = ::onDocumentEvent,
            onError = {

            })
    }

    private fun onDocumentEvent(wasDocumentDeleted: Boolean, workout: Workout) {
        if (wasDocumentDeleted) {
            workouts.remove(workout.workoutId)
        } else {
            workouts[workout.workoutId] = workout
        }
    }

    fun removeListener() {
        cloudStorageWorkoutRepository.removeListener()
    }
}