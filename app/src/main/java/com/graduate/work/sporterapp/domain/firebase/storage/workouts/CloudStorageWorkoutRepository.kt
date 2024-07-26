package com.graduate.work.sporterapp.domain.firebase.storage.workouts

import com.graduate.work.sporterapp.core.Response
import com.graduate.work.sporterapp.core.SearchWorkoutParams
import com.graduate.work.sporterapp.domain.firebase.storage.workouts.entity.Workout
import kotlinx.coroutines.flow.Flow

interface CloudStorageWorkoutRepository {
    fun getWorkouts(
        searchWorkoutParams: SearchWorkoutParams,
        userId: String,
    ): Flow<Response<List<Workout>>>
    fun getWorkout(routeId: String, onError: (Throwable) -> Unit, onSuccess: (Workout) -> Unit)
    fun saveWorkout(workout: Workout, onResult: (Throwable?) -> Unit)
    fun updateWorkout(workout: Workout, onResult: (Throwable?) -> Unit)
    fun deleteWorkout(workoutId: String, onResult: (Throwable?) -> Unit)
}