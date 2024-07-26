package com.graduate.work.sporterapp.domain.firebase.storage.workouts.usecases

import com.graduate.work.sporterapp.domain.firebase.storage.workouts.CloudStorageWorkoutRepository
import com.graduate.work.sporterapp.domain.firebase.storage.workouts.entity.Workout
import javax.inject.Inject

class GetWorkoutByIdUseCase @Inject constructor(
    private val cloudStorageWorkoutRepository: CloudStorageWorkoutRepository,
) {

    operator fun invoke(id: String, onError: (Throwable) -> Unit, onSuccess: (Workout) -> Unit) =
        cloudStorageWorkoutRepository.getWorkout(id, onError, onSuccess)
}