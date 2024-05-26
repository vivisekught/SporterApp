package com.graduate.work.sporterapp.domain.firebase.storage.workout.entity

import com.mapbox.geojson.Point

data class WorkoutRoutePoint(
    val point: Point,
    val distanceFromStart: Double,
    val speed: Double,
    val timeStamp: Long,
)
