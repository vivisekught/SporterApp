package com.graduate.work.sporterapp.domain.firebase.storage.workouts.usecases

import com.graduate.work.sporterapp.core.map.MapBoxStyle
import com.graduate.work.sporterapp.domain.firebase.auth.usecases.GetUserIdUseCase
import com.graduate.work.sporterapp.domain.firebase.storage.workouts.CloudStorageWorkoutRepository
import com.graduate.work.sporterapp.domain.firebase.storage.workouts.entity.Workout
import com.graduate.work.sporterapp.domain.maps.mapbox.usecases.GetStaticMapUrlUseCase
import javax.inject.Inject

class SaveWorkoutInFirestoreUseCase @Inject constructor(
    private val cloudStorageWorkoutRepository: CloudStorageWorkoutRepository,
    private val getStaticMapUrlUseCase: GetStaticMapUrlUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
) {

    operator fun invoke(workout: Workout, style: MapBoxStyle?, onResult: (Throwable?) -> Unit) {
        val userId = getUserIdUseCase()
        val imgUrl = getStaticMapUrlUseCase(workout, style)
        val workoutWithImg = workout.copy(routeImgUrl = imgUrl, userId = userId)
        cloudStorageWorkoutRepository.saveWorkout(workoutWithImg, onResult)
    }
}
