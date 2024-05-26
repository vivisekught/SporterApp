package com.graduate.work.sporterapp.domain.firebase.storage.workout

import com.graduate.work.sporterapp.domain.firebase.storage.workout.entity.Workout

interface CloudStorageWorkoutRepository {
    fun addListener(
        userId: String,
        onDocumentEvent: (Boolean, Workout) -> Unit,
        onError: (Throwable) -> Unit,
    )

    fun removeListener()
    fun getWorkout(routeId: String, onError: (Throwable) -> Unit, onSuccess: (Workout?) -> Unit)
    fun saveWorkout(workout: Workout, onResult: (Throwable?) -> Unit)
    fun updateWorkout(workout: Workout, onResult: (Throwable?) -> Unit)
    fun deleteWorkout(workoutId: String, onResult: (Throwable?) -> Unit)
}