package com.graduate.work.sporterapp.data.firebase.storage.workout.pojo

import com.google.firebase.firestore.DocumentId

data class WorkoutFirestoreRoutePoint(
    val lng: Double = 0.0,
    val lat: Double = 0.0,
    val altitude: Double = 0.0,
    val distanceFromStart: Double = 0.0,
    val speed: Double = 0.0,
    val timeStamp: Long = 0,
)

data class WorkoutFirestorePojo(
    @DocumentId val workoutId: String = "",
    val userId: String? = null,
    val name: String = "",
    val points: List<WorkoutFirestoreRoutePoint>? = null,
    val distance: Double = 0.0,
    val duration: Double = 0.0,
    val avgSpeed: Double = 0.0,
    val maxSpeed: Double = 0.0,
    val climb: Double = 0.0,
    val descent: Double = 0.0,
    val calories: Double = 0.0,
    val routeImgUrl: String? = null,
    val timeStamp: Long = 0,
)