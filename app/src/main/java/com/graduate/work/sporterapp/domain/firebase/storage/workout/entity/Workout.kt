package com.graduate.work.sporterapp.domain.firebase.storage.workout.entity

import kotlin.time.Duration

data class Workout(
    val workoutId: String = "",
    val userId: String? = null,
    val name: String,
    val points: List<WorkoutRoutePoint>,
    val distance: Double,
    val duration: Duration,
    val avgSpeed: Double,
    val maxSpeed: Double,
    val climb: Double,
    val descent: Double,
    val calories: Double,
    val routeImgUrl: String? = null,
    val timeStamp: Long,
)