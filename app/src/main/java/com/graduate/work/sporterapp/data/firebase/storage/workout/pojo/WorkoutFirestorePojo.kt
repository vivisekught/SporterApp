package com.graduate.work.sporterapp.data.firebase.storage.workout.pojo

import com.google.firebase.firestore.DocumentId
import kotlin.time.Duration

data class WorkoutFirestoreRoutePoint(
    val lng: Double = 0.0,
    val lat: Double = 0.0,
    val altitude: Double = 0.0,
    val distanceFromStart: Double,
    val speed: Double,
    val timeStamp: Long,
)

data class WorkoutFirestorePojo(
    @DocumentId val workoutId: String = "",
    val userId: String? = null,
    val name: String = "",
    val points: List<WorkoutFirestoreRoutePoint> = emptyList(),
    val distance: Double = 0.0,
    val duration: Duration = Duration.ZERO,
    val avgSpeed: Double = 0.0,
    val maxSpeed: Double = 0.0,
    val climb: Double = 0.0,
    val descent: Double = 0.0,
    val calories: Double = 0.0,
    val routeImgUrl: String? = null,
    val timeStamp: Long = 0,
)