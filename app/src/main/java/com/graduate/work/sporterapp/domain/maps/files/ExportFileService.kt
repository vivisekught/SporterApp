package com.graduate.work.sporterapp.domain.maps.files

import com.graduate.work.sporterapp.domain.firebase.storage.workouts.entity.Workout
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route

abstract class ExportFileService {

    abstract suspend fun importRoutes(routesPath: String): Route

    abstract suspend fun saveRouteInInternalStorage(route: Route): Boolean

    abstract suspend fun saveWorkoutInInternalStorage(workout: Workout): Boolean
}