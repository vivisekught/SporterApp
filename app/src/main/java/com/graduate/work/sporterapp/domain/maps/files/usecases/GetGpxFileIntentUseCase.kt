package com.graduate.work.sporterapp.domain.maps.files.usecases

import android.content.Context
import android.content.Intent
import com.graduate.work.sporterapp.core.ext.createFileIntent
import com.graduate.work.sporterapp.data.maps.files.GpxFileService
import com.graduate.work.sporterapp.domain.firebase.storage.workouts.entity.Workout
import com.graduate.work.sporterapp.domain.maps.mapbox.entity.Route
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetGpxFileIntentUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gpxFileService: GpxFileService,
) {

    suspend operator fun invoke(route: Route): Intent? {
        val isSaved = gpxFileService.saveRouteInInternalStorage(route)
        if (!isSaved) return null
        val fileName = route.name + ".gpx"
        return createFileIntent(context, fileName, "application/gpx+xml")
    }

    suspend operator fun invoke(workout: Workout): Intent? {
        val isSaved = gpxFileService.saveWorkoutInInternalStorage(workout)
        if (!isSaved) return null
        val fileName = workout.name + ".gpx"
        return createFileIntent(context, fileName, "application/tcx+xml")
    }
}