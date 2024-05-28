package com.graduate.work.sporterapp.domain.firebase.storage.workout.entity

data class Workout(
    val workoutId: String = "",
    val userId: String? = null,
    val name: String,
    val points: List<WorkoutRoutePoint>? = null,
    val distance: Double,
    val duration: Double,
    val avgSpeed: Double,
    val maxSpeed: Double,
    val climb: Double,
    val descent: Double,
    val calories: Double,
    val routeImgUrl: String? = null,
    val timeStamp: Long,
)